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

package plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest;

import com.teamabnormals.neapolitan.common.block.VanillaVineTopBlock;
import com.teamabnormals.neapolitan.core.registry.NeapolitanBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class VanillaHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!isVine(state))
            return false;
        var direction = state.getValue(VanillaVineTopBlock.FACING);
        BlockPos root = pos;
        while (context.level().isLoaded(root.relative(direction.getOpposite()))
                && VanillaVineTopBlock.facingSameDirection(state, context.level().getBlockState(root.relative(direction.getOpposite()))))
            root = root.relative(direction.getOpposite());
        if (!context.level().isLoaded(root.relative(direction.getOpposite()))
                || !context.level().getBlockState(root).canSurvive(context.level(), root))
            return false;
        List<BlockPos> parts = new ArrayList<>();
        BlockPos part = root;
        while (context.level().isLoaded(part)
                && VanillaVineTopBlock.facingSameDirection(state, context.level().getBlockState(part))) {
            parts.add(part);
            part = part.relative(direction);
        }
        if (!context.level().isLoaded(part) || (context.replant() && parts.size() < 2))
            return false;
        return HarvestOperations.harvestGrowingPlant(context, parts, root,
                NeapolitanBlocks.VANILLA_VINE.get().defaultBlockState().setValue(VanillaVineTopBlock.FACING, direction));
    }

    private static boolean isVine(BlockState state) {
        return state.is(NeapolitanBlocks.VANILLA_VINE.get()) || state.is(NeapolitanBlocks.VANILLA_VINE_PLANT.get());
    }
}
