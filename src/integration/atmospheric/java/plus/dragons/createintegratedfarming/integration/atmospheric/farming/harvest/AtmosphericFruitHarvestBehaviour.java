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

package plus.dragons.createintegratedfarming.integration.atmospheric.farming.harvest;

import com.teamabnormals.atmospheric.common.block.DragonRootsBlock;
import com.teamabnormals.atmospheric.common.block.PassionVineBlock;
import com.teamabnormals.atmospheric.common.block.state.properties.DragonRootsStage;
import com.teamabnormals.atmospheric.core.registry.AtmosphericBlocks;
import com.teamabnormals.atmospheric.core.registry.AtmosphericItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class AtmosphericFruitHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        var behaviour = new AtmosphericFruitHarvestBehaviour();
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.ORANGE.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.BLOOD_ORANGE.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.PASSION_VINE.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.DRAGON_ROOTS.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.YUCCA_BUNDLE.get(), behaviour);
        CustomHarvestBehaviour.REGISTRY.register(AtmosphericBlocks.HANGING_CURRANT.get(), behaviour);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        var random = context.level().random;
        if (state.is(AtmosphericBlocks.PASSION_VINE.get())) {
            return state.getValue(PassionVineBlock.AGE) == 4 && HarvestOperations.pickFruit(context, pos,
                    state.setValue(PassionVineBlock.AGE, 1), new ItemStack(AtmosphericItems.PASSION_FRUIT.get(),
                            1 + random.nextInt(2) + random.nextInt(2) + random.nextInt(3)));
        }
        if (state.is(AtmosphericBlocks.DRAGON_ROOTS.get())) {
            if (!DragonRootsBlock.hasFruit(state))
                return false;
            List<ItemStack> drops = new ArrayList<>();
            BlockState harvested = state;
            for (var property : List.of(DragonRootsBlock.TOP_STAGE, DragonRootsBlock.BOTTOM_STAGE)) {
                if (!DragonRootsBlock.hasFruit(property, state))
                    continue;
                drops.add(new ItemStack(DragonRootsBlock.isEnder(property, state)
                        ? AtmosphericItems.ENDER_DRAGON_FRUIT.get()
                        : AtmosphericItems.DRAGON_FRUIT.get()));
                harvested = harvested.setValue(property, DragonRootsStage.ROOTS);
            }
            return HarvestOperations.pickFruit(context, pos, harvested, drops.toArray(ItemStack[]::new));
        }
        if (state.is(AtmosphericBlocks.HANGING_CURRANT.get()))
            return HarvestOperations.pickFruit(context, pos, state.getFluidState().createLegacyBlock(),
                    new ItemStack(AtmosphericItems.CURRANT.get(), 2 + random.nextInt(3)));
        return (state.is(AtmosphericBlocks.ORANGE.get()) || state.is(AtmosphericBlocks.BLOOD_ORANGE.get())
                || state.is(AtmosphericBlocks.YUCCA_BUNDLE.get()))
                && HarvestOperations.harvestFruitBlock(context, pos);
    }
}
