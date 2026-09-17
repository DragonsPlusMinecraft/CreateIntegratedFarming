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

package plus.dragons.createintegratedfarming.integration.supplementaries.farming.harvest;

import java.util.List;
import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class FlaxHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof FlaxBlock flax))
            return false;
        BlockPos root = state.getValue(FlaxBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        if (!context.level().isLoaded(root) || !context.level().isLoaded(root.above()))
            return false;
        BlockState lower = context.level().getBlockState(root);
        if (!lower.is(flax) || lower.getValue(FlaxBlock.HALF) != DoubleBlockHalf.LOWER)
            return false;
        int age = flax.getAge(lower);
        if (context.harvestPartiallyGrown() ? age <= 0 : !flax.isMaxAge(lower))
            return false;
        BlockState upper = context.level().getBlockState(root.above());
        boolean tall = !flax.isSingle(lower);
        if (tall && (!upper.is(flax) || upper.getValue(FlaxBlock.HALF) != DoubleBlockHalf.UPPER || flax.getAge(upper) != age))
            return false;
        if (!tall && upper.is(flax))
            return false;
        return HarvestOperations.harvestPlant(context, tall ? List.of(root, root.above()) : List.of(root),
                root, flax.getStateForAge(0), ModRegistry.FLAX_SEEDS_ITEM.get());
    }
}
