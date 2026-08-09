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

import com.simibubi.create.AllTags.AllBlockTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public final class StandardAreaHarvests {
    private StandardAreaHarvests() {}

    public static boolean harvest(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (state.isAir() || AllBlockTags.NON_HARVESTABLE.matches(state))
            return false;

        var custom = CustomHarvestBehaviour.REGISTRY.get(state);
        if (custom != null)
            return custom.harvestInArea(context, pos, state);

        HarvestDefinition definition = getDefinition(context, pos, state);
        if (definition == null)
            return false;

        List<ItemStack> drops = new ArrayList<>();
        CustomHarvestBehaviour.harvestBlock(
                context.level(), pos, Blocks.AIR.defaultBlockState(), null, context.tool(), 1.0F, drops::add);

        if (context.replant()
                && definition.replantedState().canSurvive(context.level(), pos)
                && consumeSeed(context, drops, definition.seed()))
            context.level().setBlockAndUpdate(pos, definition.replantedState());
        drops.forEach(context::collect);
        return true;
    }

    private static @Nullable HarvestDefinition getDefinition(
            AreaHarvestContext context, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            int age = crop.getAge(state);
            if (!isReady(age, crop.getMaxAge(), context.harvestPartiallyGrown()))
                return null;
            return new HarvestDefinition(crop.getStateForAge(0), crop.asItem());
        }
        if (block instanceof CocoaBlock) {
            int age = state.getValue(CocoaBlock.AGE);
            if (!isReady(age, CocoaBlock.MAX_AGE, context.harvestPartiallyGrown()))
                return null;
            return new HarvestDefinition(state.setValue(CocoaBlock.AGE, 0), Items.COCOA_BEANS);
        }
        if (block instanceof SweetBerryBushBlock) {
            int age = state.getValue(SweetBerryBushBlock.AGE);
            if (!isReady(age, SweetBerryBushBlock.MAX_AGE, context.harvestPartiallyGrown()))
                return null;
            return new HarvestDefinition(state.setValue(SweetBerryBushBlock.AGE, 1), Items.SWEET_BERRIES);
        }
        if (block instanceof NetherWartBlock) {
            int age = state.getValue(NetherWartBlock.AGE);
            if (!isReady(age, NetherWartBlock.MAX_AGE, context.harvestPartiallyGrown()))
                return null;
            return new HarvestDefinition(state.setValue(NetherWartBlock.AGE, 0), Items.NETHER_WART);
        }
        if (!(block instanceof BushBlock)
                || block instanceof StemBlock
                || block instanceof GrowingPlantBlock
                || block instanceof SugarCaneBlock
                || !state.getCollisionShape(context.level(), pos).isEmpty())
            return null;

        IntegerProperty ageProperty = findAgeProperty(state);
        if (ageProperty == null)
            return null;
        int age = state.getValue(ageProperty);
        int maximum = ageProperty.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (!isReady(age, maximum, context.harvestPartiallyGrown()) || block.asItem() == Items.AIR)
            return null;
        return new HarvestDefinition(state.setValue(ageProperty, ageProperty.getPossibleValues().stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0)), block.asItem());
    }

    private static @Nullable IntegerProperty findAgeProperty(BlockState state) {
        for (var property : state.getProperties()) {
            if (property instanceof IntegerProperty integer
                    && property.getName().equals(BlockStateProperties.AGE_1.getName()))
                return integer;
        }
        return null;
    }

    private static boolean isReady(int age, int maximum, boolean partial) {
        return partial ? age > 0 : age == maximum;
    }

    private static boolean consumeSeed(
            AreaHarvestContext context, List<ItemStack> drops, Item seed) {
        for (ItemStack stack : drops) {
            if (!stack.is(seed))
                continue;
            stack.shrink(1);
            return true;
        }
        return !context.extractSeed(stack -> stack.is(seed), 1).isEmpty();
    }

    private record HarvestDefinition(BlockState replantedState, Item seed) {}
}
