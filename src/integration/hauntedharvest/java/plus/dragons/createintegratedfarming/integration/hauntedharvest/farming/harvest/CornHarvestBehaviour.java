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

package plus.dragons.createintegratedfarming.integration.hauntedharvest.farming.harvest;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornBaseBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornMiddleBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornTopBlock;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class CornHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof AbstractCornBlock contacted))
            return false;
        BlockPos root = pos.below(contacted.getHeight());
        for (int i = 0; i < 3; i++)
            if (!context.level().isLoaded(root.above(i)))
                return false;
        BlockState lower = context.level().getBlockState(root);
        if (!(lower.getBlock() instanceof CornBaseBlock base) || base.getAge(lower) == 0)
            return false;
        List<BlockPos> parts = new ArrayList<>(List.of(root));
        if (base.isMaxAge(lower)) {
            BlockState middle = context.level().getBlockState(root.above());
            if (!(middle.getBlock() instanceof CornMiddleBlock mid))
                return false;
            parts.add(root.above());
            if (mid.isMaxAge(middle)) {
                BlockState upper = context.level().getBlockState(root.above(2));
                if (!(upper.getBlock() instanceof CornTopBlock))
                    return false;
                parts.add(root.above(2));
            } else if (context.level().getBlockState(root.above(2)).getBlock() instanceof AbstractCornBlock)
                return false;
        } else if (context.level().getBlockState(root.above()).getBlock() instanceof AbstractCornBlock)
            return false;
        if (!context.harvestPartiallyGrown() && !base.isPlantFullyGrown(lower, root, context.level()))
            return false;
        return HarvestOperations.harvestPlant(context, parts, root, base.getStateForAge(0), ModRegistry.KERNELS.get());
    }
}
