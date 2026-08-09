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

package plus.dragons.createintegratedfarming.integration.festivedelight.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.toopa.festivedelight.init.FestiveDelightModBlocks;
import net.toopa.festivedelight.init.FestiveDelightModItems;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class CinnamonHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        if (!isRipe(context.world, pos))
            return;
        behaviour.collectOrDropItem(context, new ItemStack(FestiveDelightModItems.CINNAMON_STICKS.get()));
        reset(context.world, pos);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!isRipe(context.level(), pos))
            return false;
        context.collect(new ItemStack(FestiveDelightModItems.CINNAMON_STICKS.get()));
        reset(context.level(), pos);
        return true;
    }

    private static boolean isRipe(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(FestiveDelightModBlocks.CINNAMON_BUSHRIPE.get());
    }

    private static void reset(Level level, BlockPos pos) {
        if (!isRipe(level, pos))
            return;
        level.setBlock(pos, FestiveDelightModBlocks.CINNAMON_BUSH.get().defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.NEUTRAL, 1.0F, 60.0F);
    }
}
