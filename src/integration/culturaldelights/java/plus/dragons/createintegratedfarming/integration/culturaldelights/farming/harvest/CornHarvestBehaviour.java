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
import com.baisylia.culturaldelights.block.custom.CornBlock;
import com.baisylia.culturaldelights.block.custom.CornUpperBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class CornHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        CornParts parts = findMatureParts(context.world, pos);
        if (parts == null)
            return;
        BlockState harvestedState = CustomHarvestBehaviour.replant()
                ? parts.upperState().setValue(CornUpperBlock.CORN_AGE, 0)
                : Blocks.AIR.defaultBlockState();
        CustomHarvestBehaviour.harvestBlock(
                context.world, parts.upperPos(), harvestedState, null,
                CustomHarvestBehaviour.getHarvestTool(context), 1.0F,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        CornParts parts = findMatureParts(context.level(), pos);
        if (parts == null)
            return false;
        BlockState harvestedState = context.replant()
                ? parts.upperState().setValue(CornUpperBlock.CORN_AGE, 0)
                : Blocks.AIR.defaultBlockState();
        CustomHarvestBehaviour.harvestBlock(
                context.level(), parts.upperPos(), harvestedState, null,
                context.tool(), 1.0F, context::collect);
        return true;
    }

    private static @Nullable CornParts findMatureParts(Level level, BlockPos contactedPos) {
        BlockState contactedState = level.getBlockState(contactedPos);
        BlockPos rootPos;
        if (contactedState.is(ModBlocks.CORN.get()))
            rootPos = contactedPos;
        else if (contactedState.is(ModBlocks.CORN_UPPER.get()))
            rootPos = contactedPos.below();
        else
            return null;

        BlockPos upperPos = rootPos.above();
        BlockState rootState = level.getBlockState(rootPos);
        BlockState upperState = level.getBlockState(upperPos);
        if (!rootState.is(ModBlocks.CORN.get()) || !upperState.is(ModBlocks.CORN_UPPER.get()))
            return null;
        if (rootState.getValue(CornBlock.AGE) != 3 || upperState.getValue(CornUpperBlock.CORN_AGE) != 3)
            return null;
        return new CornParts(upperPos, upperState);
    }

    private record CornParts(BlockPos upperPos, BlockState upperState) {}
}
