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

package plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest;

import alabaster.hearthandharvest.common.block.trellis.GrapeTrellisBlock;
import alabaster.hearthandharvest.common.block.trellis.TrellisPlant;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModItems;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class GrapeHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockState mature = getMatureState(context.world, pos);
        if (mature == null)
            return;
        behaviour.collectOrDropItem(context, createDrop(context.world, mature));
        reset(context.world, pos, mature);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState mature = getMatureState(context.level(), pos);
        if (mature == null)
            return false;
        context.collect(createDrop(context.level(), mature));
        reset(context.level(), pos, mature);
        return true;
    }

    private static @Nullable BlockState getMatureState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(HHModBlocks.GRAPE_TRELLIS.get()) || state.getValue(GrapeTrellisBlock.AGE) != 4)
            return null;
        return state.getValue(GrapeTrellisBlock.PLANT).isGrape() ? state : null;
    }

    private static ItemStack createDrop(Level level, BlockState state) {
        Item grapes = state.getValue(GrapeTrellisBlock.PLANT) == TrellisPlant.RED_GRAPE
                ? HHModItems.RED_GRAPES.get()
                : HHModItems.GREEN_GRAPES.get();
        return new ItemStack(grapes, 1 + level.random.nextInt(2));
    }

    private static void reset(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(GrapeTrellisBlock.AGE, 0), Block.UPDATE_ALL);
        level.playSound(
                null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 0.8F + level.random.nextFloat() * 0.4F);
    }
}
