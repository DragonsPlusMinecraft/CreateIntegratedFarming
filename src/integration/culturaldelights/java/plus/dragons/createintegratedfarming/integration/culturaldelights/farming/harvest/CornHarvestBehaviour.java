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
import com.baisylia.culturaldelights.item.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class CornHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!state.is(ModBlocks.CORN.get()))
            return false;
        int age = state.getValue(CornBlock.AGE);
        if (age == 0 || (age < 7 && !context.harvestPartiallyGrown())
                || state.getValue(CornBlock.HEIGHT) > CornBlock.getExpectedMaxHeight(age))
            return false;
        BlockPos root = pos.below(state.getValue(CornBlock.HEIGHT));
        List<BlockPos> parts = new ArrayList<>();
        for (int height = 0; height <= CornBlock.getExpectedMaxHeight(age); height++) {
            BlockPos part = root.above(height);
            if (!context.level().isLoaded(part))
                return false;
            BlockState partState = context.level().getBlockState(part);
            if (!partState.is(ModBlocks.CORN.get()) || partState.getValue(CornBlock.HEIGHT) != height
                    || partState.getValue(CornBlock.AGE) != age)
                return false;
            parts.add(part);
        }
        return HarvestOperations.harvestPlant(context, parts, root, ModBlocks.CORN.get().defaultBlockState(),
                ModItems.CORN_KERNELS.get());
    }
}
