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

package plus.dragons.createintegratedfarming.integration.endersdelight.farming.harvest;

import com.axedgaming.endersdelight.Common.Blocks.Bush.ChorusFlameBlock;
import com.axedgaming.endersdelight.Common.Blocks.Bush.VoidPepperBush;
import com.axedgaming.endersdelight.Common.Registries.EdBlocks;
import com.axedgaming.endersdelight.Common.Registries.EdItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class EndersHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        var behaviour = new EndersHarvestBehaviour();
        CustomHarvestBehaviour.REGISTRY.register(EdBlocks.AMBERVEIL_MUSHROOM.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(EdBlocks.CHORUS_FLAME_BUSH.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(EdBlocks.ETHEREAL_SAFFRON_BUSH.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(EdBlocks.VOID_PEPPER_BUSH.get(), behaviour);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (state.is(EdBlocks.VOID_PEPPER_BUSH.get())) {
            if (!state.getValue(VoidPepperBush.PEPPER))
                return false;
            return HarvestOperations.pickFruit(context, pos,
                    state.setValue(VoidPepperBush.PEPPER, false).setValue(VoidPepperBush.SPREADING, true),
                    new ItemStack(EdItems.VOIDPEPPER.get()));
        }
        if (state.is(EdBlocks.CHORUS_FLAME_BUSH.get())) {
            int age = state.getValue(ChorusFlameBlock.AGE);
            if (age == 0 || (age < ChorusFlameBlock.MAX_AGE && !context.harvestPartiallyGrown()))
                return false;
            return HarvestOperations.harvestPlant(context, List.of(pos), pos,
                    state.setValue(ChorusFlameBlock.AGE, 0), EdItems.CHORUSFLAME.get());
        }
        if (state.is(EdBlocks.AMBERVEIL_MUSHROOM.get()))
            return HarvestOperations.harvestPlant(context, List.of(pos), pos, state, EdItems.AMBERVEIL.get());
        if (state.is(EdBlocks.ETHEREAL_SAFFRON_BUSH.get()))
            return HarvestOperations.harvestPlant(context, List.of(pos), pos, state, EdItems.ETHEREAL_SAFFRON.get());
        return false;
    }
}
