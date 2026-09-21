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
import com.baisylia.culturaldelights.item.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import vectorwing.farmersdelight.common.block.TomatoBlock;

public class BeansHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(context.level() instanceof ServerLevel level) || !isBeans(state))
            return false;
        int age = state.getValue(TomatoBlock.VINE_AGE);
        if (age == 0 || (age < 3 && !context.harvestPartiallyGrown()))
            return false;
        if (context.replant())
            return HarvestOperations.pickFruit(context, pos, state.setValue(TomatoBlock.VINE_AGE, 0),
                    age == 3 ? pods(context) : ItemStack.EMPTY);

        List<BlockPos> parts = new ArrayList<>();
        BlockPos part = pos;
        while (context.level().isLoaded(part) && isBeans(context.level().getBlockState(part))) {
            parts.add(part);
            part = part.above();
        }
        if (!context.level().isLoaded(part) || !HarvestOperations.canHarvest(context, parts))
            return false;
        // Clear from the top so removing a lower vine cannot break an uncollected upper vine.
        for (BlockPos harvestedPos : parts.reversed()) {
            BlockState harvestedState = context.level().getBlockState(harvestedPos);
            BlockState support = restoredSupport(harvestedState);
            // Cultural Delights does not provide separate loot tables for the support variants.
            BlockState lootState = ModBlocks.BEANS.get().withPropertiesOf(harvestedState)
                    .setValue(TomatoBlock.ROPELOGGED, !support.isAir());
            List<ItemStack> drops = Block.getDrops(lootState, level, harvestedPos, null, null, context.tool());
            HarvestOperations.pickFruit(context, harvestedPos, support, drops.toArray(ItemStack[]::new));
        }
        return true;
    }

    private static ItemStack pods(AreaHarvestContext context) {
        return new ItemStack(ModItems.BEAN_POD.get(), 1 + context.level().random.nextInt(2));
    }

    private static boolean isBeans(BlockState state) {
        return state.is(ModBlocks.BEANS.get()) || state.is(ModBlocks.ROPE_BEANS.get()) || state.is(ModBlocks.STICK_BEANS.get());
    }

    private static BlockState restoredSupport(BlockState state) {
        if (ModIntegration.SUPPLEMENTARIES.enabled()) {
            if (state.is(ModBlocks.ROPE_BEANS.get()))
                return BuiltInRegistries.BLOCK.get(ModIntegration.SUPPLEMENTARIES.asResource("rope")).withPropertiesOf(state);
            if (state.is(ModBlocks.STICK_BEANS.get()))
                return BuiltInRegistries.BLOCK.get(ModIntegration.SUPPLEMENTARIES.asResource("stick")).withPropertiesOf(state);
        }
        if (state.getValue(TomatoBlock.ROPELOGGED))
            return vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get().defaultBlockState();
        return state.getFluidState().createLegacyBlock();
    }
}
