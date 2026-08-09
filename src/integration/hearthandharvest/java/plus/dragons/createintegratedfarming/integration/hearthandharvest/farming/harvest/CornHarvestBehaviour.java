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

import alabaster.hearthandharvest.common.block.CornStalkBlock;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModItems;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class CornHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockState mature = getMatureState(context.world, pos);
        if (mature == null)
            return;
        behaviour.collectOrDropItem(context, createDrop(mature));
        reset(context.world, pos, mature);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState mature = getMatureState(context.level(), pos);
        if (mature == null)
            return false;
        context.collect(createDrop(mature));
        reset(context.level(), pos, mature);
        return true;
    }

    private static @Nullable BlockState getMatureState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(HHModBlocks.CORN_STALK.get()) || state.getValue(CornStalkBlock.AGE) < 4)
            return null;
        return state;
    }

    private static ItemStack createDrop(BlockState state) {
        int count = state.getValue(CornStalkBlock.AGE) == 5 ? 2 : 1;
        return new ItemStack(HHModItems.CORN.get(), count);
    }

    private static void reset(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(CornStalkBlock.AGE, 3), 3);
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
