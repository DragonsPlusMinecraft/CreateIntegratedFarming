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

package plus.dragons.createintegratedfarming.integration.culturaldelights.farming.harvest;

import com.baisylia.culturaldelights.block.ModBlocks;
import com.baisylia.culturaldelights.block.custom.FruitingLeaves;
import com.baisylia.culturaldelights.item.ModItems;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class AvocadoHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        Level level = context.world;
        if (!isMature(level, pos))
            return;
        behaviour.collectOrDropItem(context, createDrop(level));
        reset(level, pos);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        Level level = context.level();
        if (!isMature(level, pos))
            return false;
        context.collect(createDrop(level));
        reset(level, pos);
        return true;
    }

    private static boolean isMature(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlocks.FRUITING_AVOCADO_LEAVES.get()) && state.getValue(FruitingLeaves.AGE) == 4;
    }

    private static ItemStack createDrop(Level level) {
        return new ItemStack(ModItems.AVOCADO.get(), 2 + level.random.nextInt(2));
    }

    private static void reset(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.FRUITING_AVOCADO_LEAVES.get()) || state.getValue(FruitingLeaves.AGE) != 4)
            return;
        level.playSound(
                null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        level.setBlock(pos, state.setValue(FruitingLeaves.AGE, 1), 2);
    }
}
