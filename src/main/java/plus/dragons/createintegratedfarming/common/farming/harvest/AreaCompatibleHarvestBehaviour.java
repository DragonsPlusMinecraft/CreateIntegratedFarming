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

package plus.dragons.createintegratedfarming.common.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

/** Shares one crop implementation between mechanical and area harvesters. */
public abstract class AreaCompatibleHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public final void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        harvestInArea(new AreaHarvestContext(context.world, CustomHarvestBehaviour.replant(),
                CustomHarvestBehaviour.partial(), CustomHarvestBehaviour.getHarvestTool(context),
                stack -> behaviour.collectOrDropItem(context, stack),
                (predicate, amount) -> ItemHelper.extract(context.contraption.getStorage().getAllItems(), predicate, amount, false)),
                pos, state);
    }

    @Override
    public abstract boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state);
}
