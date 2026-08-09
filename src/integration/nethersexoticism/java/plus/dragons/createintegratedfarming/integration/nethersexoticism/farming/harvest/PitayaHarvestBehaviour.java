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
import javax.annotation.Nullable;
import net.mcreator.nethersexoticism.block.PitayaBlockBlock;
import net.mcreator.nethersexoticism.block.PitayaBlockOpenBlock;
import net.mcreator.nethersexoticism.init.NethersExoticismModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class PitayaHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockState fruit = getFruit(context.world, pos);
        if (fruit == null)
            return;
        CustomHarvestBehaviour.harvestBlock(
                context.world, pos, replacement(fruit, context.motion), null,
                CustomHarvestBehaviour.getHarvestTool(context), 1.0F,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState fruit = getFruit(context.level(), pos);
        if (fruit == null)
            return false;
        CustomHarvestBehaviour.harvestBlock(
                context.level(), pos, replacement(fruit, null), null,
                context.tool(), 1.0F, context::collect);
        return true;
    }

    private static @Nullable BlockState getFruit(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(NethersExoticismModBlocks.PITAYA_BLOCK.get()) ||
                state.is(NethersExoticismModBlocks.PITAYA_BLOCK_OPEN.get()))
            return state;
        return null;
    }

    private static BlockState replacement(BlockState state, @Nullable Vec3 motion) {
        if (state.is(NethersExoticismModBlocks.PITAYA_BLOCK_OPEN.get()))
            return state.getValue(PitayaBlockOpenBlock.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();

        BlockState open = NethersExoticismModBlocks.PITAYA_BLOCK_OPEN.get().defaultBlockState()
                .setValue(PitayaBlockOpenBlock.WATERLOGGED, state.getValue(PitayaBlockBlock.WATERLOGGED));
        Direction facing = facingFromMotion(motion);
        return facing == null ? open : open.setValue(PitayaBlockOpenBlock.FACING, facing);
    }

    private static @Nullable Direction facingFromMotion(@Nullable Vec3 motion) {
        if (motion == null || motion.x * motion.x + motion.z * motion.z < 1.0E-6)
            return null;
        Direction movement;
        if (Math.abs(motion.x) > Math.abs(motion.z))
            movement = motion.x > 0 ? Direction.EAST : Direction.WEST;
        else
            movement = motion.z > 0 ? Direction.SOUTH : Direction.NORTH;
        return movement.getOpposite();
    }
}
