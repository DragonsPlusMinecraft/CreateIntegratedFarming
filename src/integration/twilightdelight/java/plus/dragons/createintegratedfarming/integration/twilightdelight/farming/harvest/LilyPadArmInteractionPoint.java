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

package plus.dragons.createintegratedfarming.integration.twilightdelight.farming.harvest;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class LilyPadArmInteractionPoint extends ArmInteractionPoint {
    private static final ResourceLocation LILY_PAD = ResourceLocation.withDefaultNamespace("lily_pad");
    private static final ResourceLocation HUGE_LILY_PAD = ModIntegration.TWILIGHT_FOREST.asResource("huge_lily_pad");
    private static final ResourceLocation HUGE_WATER_LILY = ModIntegration.TWILIGHT_FOREST.asResource("huge_water_lily");
    private static final Set<ResourceLocation> HARVESTABLE_LILIES = Set.of(
            LILY_PAD,
            HUGE_LILY_PAD,
            HUGE_WATER_LILY);
    private static final Direction[] HARVEST_DIRECTIONS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    public LilyPadArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public Mode getMode() {
        return Mode.TAKE;
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        HarvestTarget target = findHarvestTarget();
        if (target == null)
            return ItemStack.EMPTY;
        if (!simulate)
            level.destroyBlock(target.pos(), false);
        return target.stack().copy();
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 1;
    }

    @Nullable
    private HarvestTarget findHarvestTarget() {
        for (Direction direction : HARVEST_DIRECTIONS) {
            BlockPos targetPos = pos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            if (!HARVESTABLE_LILIES.contains(BuiltInRegistries.BLOCK.getKey(targetState.getBlock())))
                continue;
            ItemStack stack = targetState.getBlock().asItem().getDefaultInstance();
            if (!stack.isEmpty())
                return new HarvestTarget(targetPos, stack);
        }
        return null;
    }

    private record HarvestTarget(BlockPos pos, ItemStack stack) {}

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(HUGE_WATER_LILY);
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new LilyPadArmInteractionPoint(this, level, pos, state);
        }
    }
}
