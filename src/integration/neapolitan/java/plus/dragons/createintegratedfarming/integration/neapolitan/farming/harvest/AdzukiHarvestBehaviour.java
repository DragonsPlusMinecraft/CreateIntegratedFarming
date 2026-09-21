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

import com.teamabnormals.neapolitan.common.block.AdzukiSproutsBlock;
import com.teamabnormals.neapolitan.core.registry.NeapolitanBlocks;
import com.teamabnormals.neapolitan.core.registry.NeapolitanItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class AdzukiHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!state.is(NeapolitanBlocks.ADZUKI_SPROUTS.get()))
            return false;
        int age = state.getValue(AdzukiSproutsBlock.AGE);
        if (age == 0 || (age < 6 && !context.harvestPartiallyGrown()))
            return false;
        return HarvestOperations.harvestPlant(context, List.of(pos), pos,
                NeapolitanBlocks.ADZUKI_SPROUTS.get().defaultBlockState(), NeapolitanItems.ADZUKI_BEANS.get());
    }
}
