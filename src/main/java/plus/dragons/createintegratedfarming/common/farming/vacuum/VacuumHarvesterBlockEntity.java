/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class VacuumHarvesterBlockEntity extends KineticBlockEntity {
    private static final int INVENTORY_SIZE = 18;
    private final ItemStackHandler inventory;
    private final IItemHandler outputHandler;
    private final List<ItemStack> pendingDrops = new ArrayList<>();
    private double chargeProgress;
    private int releaseTicks;
    private boolean chargingBlocked;
    private float previousHeadOffset;
    private float headOffset;

    public VacuumHarvesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null)
                    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
        };
        outputHandler = new ItemHandlerWrapper(inventory) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        return side == Direction.DOWN ? null : outputHandler;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null)
            return;
        if (level.isClientSide) {
            tickClientCycle();
            return;
        }

        if (releaseTicks > 0) {
            if (isPowered()) {
                releaseTicks--;
                if (releaseTicks == 0) {
                    chargeProgress = 0;
                    sendData();
                }
                setChanged();
            }
            return;
        }

        setChargingBlocked(!flushPendingDrops());
        if (chargingBlocked || !isPowered())
            return;

        chargeProgress = VacuumHarvesterCycle.advanceCharge(
                chargeProgress, VacuumHarvesterCycle.stationaryChargeIncrement(getSpeed()));
        if (chargeProgress >= 1) {
            releaseTicks = VacuumHarvesterCycle.RELEASE_DURATION;
            sendData();
            harvestArea();
        }
        setChanged();
    }

    private void tickClientCycle() {
        previousHeadOffset = headOffset;
        if (releaseTicks > 0) {
            if (isPowered()) {
                releaseTicks--;
                if (releaseTicks == 0)
                    chargeProgress = 0;
            }
        } else if (!chargingBlocked && isPowered()) {
            chargeProgress = VacuumHarvesterCycle.advanceCharge(
                    chargeProgress, VacuumHarvesterCycle.stationaryChargeIncrement(getSpeed()));
        }
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);

        if (releaseTicks == 0
                && !chargingBlocked
                && isPowered()
                && Math.floorMod(level.getGameTime() + worldPosition.hashCode(), 8) == 0)
            VacuumHarvesterEffects.spawnExhaust(
                    level, VacuumHarvesterEffects.intake(worldPosition, headOffset));
    }

    private boolean isPowered() {
        return getSpeed() != 0 && !isOverStressed();
    }

    private void harvestArea() {
        int range = CIFConfig.server().vacuumHarvesterRange.get();

        AreaHarvestContext context = new AreaHarvestContext(
                level,
                true,
                false,
                ItemStack.EMPTY,
                this::collectDrop,
                this::extractSeed);
        var result = VacuumHarvesterHarvesting.harvestArea(context, worldPosition, range);
        VacuumHarvesterEffects.emitSuction(
                (ServerLevel) level,
                VacuumHarvesterEffects.intake(worldPosition, VacuumHarvesterCycle.MAX_HEAD_OFFSET),
                result.particleSources());
    }

    private void setChargingBlocked(boolean blocked) {
        if (chargingBlocked == blocked)
            return;
        chargingBlocked = blocked;
        sendData();
    }

    private void collectDrop(ItemStack stack) {
        if (stack.isEmpty())
            return;
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, stack, false);
        if (!remainder.isEmpty())
            pendingDrops.add(remainder.copy());
    }

    private ItemStack extractSeed(Predicate<ItemStack> predicate, int amount) {
        return ItemHelper.extract(inventory, predicate, amount, false);
    }

    private boolean flushPendingDrops() {
        if (pendingDrops.isEmpty())
            return true;
        int index = 0;
        while (index < pendingDrops.size()) {
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, pendingDrops.get(index), false);
            if (remainder.isEmpty()) {
                pendingDrops.remove(index);
            } else {
                pendingDrops.set(index, remainder);
                index++;
            }
        }
        setChanged();
        return pendingDrops.isEmpty();
    }

    public boolean giveContentsTo(net.minecraft.world.entity.player.Player player) {
        boolean moved = false;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, inventory.getSlotLimit(slot), false);
            if (stack.isEmpty())
                continue;
            player.getInventory().placeItemBackInInventory(stack);
            moved = true;
        }
        if (!pendingDrops.isEmpty()) {
            pendingDrops.forEach(stack -> player.getInventory().placeItemBackInInventory(stack));
            pendingDrops.clear();
            moved = true;
        }
        if (moved) {
            chargingBlocked = false;
            setChanged();
            sendData();
        }
        return moved;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putDouble(VacuumHarvesterCycle.CHARGE_PROGRESS, chargeProgress);
        tag.putInt(VacuumHarvesterCycle.RELEASE_TICKS, releaseTicks);
        tag.putBoolean("ChargingBlocked", chargingBlocked);
        ListTag pending = new ListTag();
        pendingDrops.forEach(stack -> pending.add(stack.save(registries, new CompoundTag())));
        tag.put("PendingDrops", pending);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        chargeProgress = Mth.clamp(tag.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS), 0, 1);
        releaseTicks = Mth.clamp(
                tag.getInt(VacuumHarvesterCycle.RELEASE_TICKS), 0, VacuumHarvesterCycle.RELEASE_DURATION);
        chargingBlocked = tag.getBoolean("ChargingBlocked");
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);
        previousHeadOffset = headOffset;
        pendingDrops.clear();
        ListTag pending = tag.getList("PendingDrops", Tag.TAG_COMPOUND);
        for (int i = 0; i < pending.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, pending.getCompound(i));
            if (!stack.isEmpty())
                pendingDrops.add(stack);
        }
    }

    public float getRenderedHeadOffset(float partialTicks) {
        return Mth.lerp(partialTicks, previousHeadOffset, headOffset);
    }

    public double getChargeProgress() {
        return chargeProgress;
    }

    public int getReleaseTicks() {
        return releaseTicks;
    }

    public void setCycleProgress(double progress, int releaseTicks) {
        chargeProgress = Mth.clamp(progress, 0, 1);
        this.releaseTicks = Mth.clamp(releaseTicks, 0, VacuumHarvesterCycle.RELEASE_DURATION);
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, this.releaseTicks);
        previousHeadOffset = headOffset;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
        if (level != null)
            pendingDrops.forEach(stack -> Containers.dropItemStack(
                    level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, stack));
    }
}
