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

package plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import vectorwing.farmersdelight.common.block.RiceBlock;
import vectorwing.farmersdelight.common.block.RicePaniclesBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class RiceHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockPos root = state.is(ModBlocks.RICE_CROP.get()) ? pos : pos.below();
        BlockPos upper = root.above();
        if (!HarvestOperations.canHarvest(context, List.of(root, upper)))
            return false;
        BlockState rootState = context.level().getBlockState(root);
        BlockState panicles = context.level().getBlockState(upper);
        if (!rootState.is(ModBlocks.RICE_CROP.get()) || rootState.getValue(RiceBlock.AGE) != 3
                || !panicles.is(ModBlocks.RICE_CROP_PANICLES.get()))
            return false;
        int age = panicles.getValue(RicePaniclesBlock.RICE_AGE);
        if ((context.harvestPartiallyGrown() ? age <= 0 : age != 3) || !context.claimHarvest(root))
            return false;
        CustomHarvestBehaviour.harvestBlock(context.level(), upper,
                context.replant() ? panicles.setValue(RicePaniclesBlock.RICE_AGE, 0) : panicles.getFluidState().createLegacyBlock(),
                null, context.tool(), 1, context::collect);
        return true;
    }
}
