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

import net.jadenxgamer.netherexp.config.JNEConfigs;
import net.jadenxgamer.netherexp.core.block.CerebrageSkullBlock;
import net.jadenxgamer.netherexp.registry.JNEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class CerebrageHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(CerebrageSkullBlock.AGE);
        if (age < 3 || (age == 4 && (!context.level().isLoaded(pos.above()) || !context.level().isEmptyBlock(pos.above()))))
            return false;
        int maximum = JNEConfigs.MAX_CEREBRAGE_DROPPED.get();
        int minimum = JNEConfigs.MIN_CEREBRAGE_DROPPED.get();
        int count = age == 3 && maximum > 0
                ? (minimum < maximum ? context.level().random.nextInt(minimum, maximum) : maximum)
                : 0;
        return HarvestOperations.pickFruit(context, pos, state.setValue(CerebrageSkullBlock.AGE, 1),
                new ItemStack(JNEItems.CEREBRAGE.get(), count),
                context.level().random.nextDouble() < JNEConfigs.CEREBRAGE_SEEDS_DROP_CHANCE.get()
                        ? new ItemStack(JNEItems.CEREBRAGE_SEEDS.get())
                        : ItemStack.EMPTY);
    }
}
