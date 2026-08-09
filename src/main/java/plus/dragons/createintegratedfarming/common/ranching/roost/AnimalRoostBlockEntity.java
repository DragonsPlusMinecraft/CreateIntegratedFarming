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

package plus.dragons.createintegratedfarming.common.ranching.roost;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public abstract class AnimalRoostBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected final ItemStackHandler inventory;
    public final IItemHandler outputHandler;
    protected int feedCooldown;
    protected int eggTime;
    protected boolean outputInventoryBlocked;

    protected int minimumProductionTicks() {
        return 6000;
    }

    protected int maximumProductionTicks() {
        return 11999;
    }

    protected SoundEvent productionSound() {
        return SoundEvents.CHICKEN_EGG;
    }

    protected abstract ResourceKey<LootTable> productionLootTable();

    public AnimalRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        eggTime = maximumProductionTicks() + 1;
        setLazyTickRate(20);
        this.inventory = new ItemStackHandler(CIFConfig.server().roostingInventorySlotCount.get()) {
            @Override
            public int getSlotLimit(int slot) {
                return CIFConfig.server().roostingInventorySlotSize.get();
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (outputInventoryBlocked) {
                    outputInventoryBlocked = false;
                    if (level != null && !level.isClientSide)
                        notifyUpdate();
                }
            }
        };
        this.outputHandler = new ItemHandlerWrapper(inventory) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction direction) {
        if (direction == getBlockState().getValue(HorizontalDirectionalBlock.FACING))
            return null;
        return outputHandler;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
                .onlyInsertWhen(side -> side == getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite())
                .considerOccupiedWhen(side -> feedCooldown > 0)
                .setInsertionHandler(this::tryInsertFrom));
    }

    @Override
    public void initialize() {
        assert level != null;
        super.initialize();
        if (eggTime > maximumProductionTicks()) {
            eggTime = nextProductionTime();
        }
    }

    @Override
    public void lazyTick() {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        boolean changed = false;
        if (feedCooldown > 0) {
            feedCooldown = Math.max(0, feedCooldown - lazyTickRate);
            changed = true;
        }
        if (eggTime > 0) {
            eggTime = Math.max(0, eggTime - lazyTickRate);
            changed = true;
        }
        if (eggTime <= 0) {
            boolean wasBlocked = outputInventoryBlocked;
            boolean inserted = false;
            var remainders = new ArrayList<ItemStack>();
            var lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(productionLootTable());
            var lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                    .withParameter(LootContextParams.ORIGIN, worldPosition.getCenter())
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, this)
                    .create(LootContextParamSets.BLOCK);
            var lootStacks = lootTable.getRandomItems(lootParams);
            for (var stack : lootStacks) {
                ItemStack remainder = ItemHandlerHelper.insertItem(inventory, stack, false);
                inserted |= stack.getCount() != remainder.getCount();
                if (!remainder.isEmpty()) remainders.add(remainder);
            }
            if (inserted) {
                outputInventoryBlocked = false;
                for (var remainder : remainders)
                    Containers.dropItemStack(
                            serverLevel,
                            worldPosition.getX() + 0.5,
                            worldPosition.getY() + 0.5,
                            worldPosition.getZ() + 0.5,
                            remainder);
                eggTime = nextProductionTime();
                level.playSound(
                        null, worldPosition, productionSound(), SoundSource.BLOCKS,
                        1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
                changed = true;
            } else {
                outputInventoryBlocked = !lootStacks.isEmpty();
            }
            changed |= wasBlocked != outputInventoryBlocked;
        }
        if (changed)
            notifyUpdate();
    }

    @Override
    protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("EggLayTime", eggTime);
        tag.putInt("FeedCooldown", feedCooldown);
        tag.putBoolean("OutputInventoryBlocked", outputInventoryBlocked);
    }

    @Override
    protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        eggTime = Math.clamp(tag.getInt("EggLayTime"), 0, maximumProductionTicks());
        feedCooldown = tag.getInt("FeedCooldown");
        outputInventoryBlocked = tag.getBoolean("OutputInventoryBlocked");
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
    }

    protected ItemStack tryInsertFrom(TransportedItemStack transported, Direction side, boolean simulate) {
        assert level != null;
        ItemStack stack = transported.stack.copy();
        if (feedItem(stack, simulate)) {
            if (!simulate) stack.shrink(1);
        }
        return stack;
    }

    public abstract boolean feedItem(ItemStack stack, boolean simulate);

    protected int nextProductionTime() {
        assert level != null;
        int minimum = minimumProductionTicks();
        return minimum + level.random.nextInt(maximumProductionTicks() - minimum + 1);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Component status;
        if (eggTime > 0) {
            int seconds = (eggTime + 19) / 20;
            String remaining = String.format("%d:%02d", seconds / 60, seconds % 60);
            status = Component.translatable("create_integrated_farming.goggles.roost.next_output", remaining)
                    .withStyle(ChatFormatting.GRAY);
        } else if (outputInventoryBlocked) {
            status = Component.translatable("create_integrated_farming.goggles.roost.output_inventory_full")
                    .withStyle(ChatFormatting.RED);
        } else {
            status = Component.translatable("create_integrated_farming.goggles.roost.ready")
                    .withStyle(ChatFormatting.GOLD);
        }
        tooltip.add(Component.literal(" ").append(status));
        return true;
    }
}
