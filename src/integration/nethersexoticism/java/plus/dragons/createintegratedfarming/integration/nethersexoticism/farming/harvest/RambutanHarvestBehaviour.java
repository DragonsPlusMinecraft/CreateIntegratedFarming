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

package plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.mcreator.nethersexoticism.block.RamboutanBlockBlock;
import net.mcreator.nethersexoticism.init.NethersExoticismModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class RambutanHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockState fruit = getFruit(context.world, pos);
        if (fruit == null)
            return;
        CustomHarvestBehaviour.harvestBlock(
                context.world, pos, replacement(fruit), null,
                CustomHarvestBehaviour.getHarvestTool(context), 1.0F,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState fruit = getFruit(context.level(), pos);
        if (fruit == null)
            return false;
        CustomHarvestBehaviour.harvestBlock(
                context.level(), pos, replacement(fruit), null,
                context.tool(), 1.0F, context::collect);
        return true;
    }

    private static @Nullable BlockState getFruit(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(NethersExoticismModBlocks.RAMBOUTAN_BLOCK.get()) ? state : null;
    }

    private static BlockState replacement(BlockState state) {
        return state.getValue(RamboutanBlockBlock.WATERLOGGED)
                ? Blocks.WATER.defaultBlockState()
                : Blocks.AIR.defaultBlockState();
    }
}
