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

package plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest;

import java.util.List;
import net.jadenxgamer.netherexp.core.block.WarpedWartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class WarpedWartHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof WarpedWartBlock wart))
            return false;
        BlockPos anchor = state.getValue(WarpedWartBlock.HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
        if (!context.level().isLoaded(anchor) || !context.level().isLoaded(anchor.below()))
            return false;
        BlockState upper = context.level().getBlockState(anchor);
        if (!upper.is(wart) || upper.getValue(WarpedWartBlock.HALF) != DoubleBlockHalf.UPPER)
            return false;
        int age = upper.getValue(WarpedWartBlock.AGE);
        if (context.harvestPartiallyGrown() ? age <= 0 : age != 3)
            return false;
        BlockState lower = context.level().getBlockState(anchor.below());
        if (age == 3 && (!lower.is(wart) || lower.getValue(WarpedWartBlock.HALF) != DoubleBlockHalf.LOWER
                || lower.getValue(WarpedWartBlock.AGE) != 3))
            return false;
        if (age < 3 && lower.is(wart))
            return false;
        return HarvestOperations.harvestPlant(context, age == 3 ? List.of(anchor, anchor.below()) : List.of(anchor),
                anchor, wart.defaultBlockState(), wart.asItem());
    }
}
