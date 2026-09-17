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

import com.teamabnormals.atmospheric.common.block.AloeVeraBlock;
import com.teamabnormals.atmospheric.common.block.AloeVeraTallBlock;
import com.teamabnormals.atmospheric.core.other.tags.AtmosphericBlockTags;
import com.teamabnormals.atmospheric.core.registry.AtmosphericBlocks;
import com.teamabnormals.atmospheric.core.registry.AtmosphericItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class AloeHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        boolean tall = state.getBlock() instanceof AloeVeraTallBlock;
        if (!tall && !(state.getBlock() instanceof AloeVeraBlock))
            return false;
        BlockPos root = tall && state.getValue(AloeVeraTallBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        List<BlockPos> parts = tall ? List.of(root, root.above()) : List.of(root);
        if (!HarvestOperations.canHarvest(context, parts) || !context.level().isLoaded(root.below()))
            return false;
        BlockState lower = context.level().getBlockState(root);
        int blossoms = 0;
        if (tall) {
            BlockState upper = context.level().getBlockState(root.above());
            if (!lower.is(AtmosphericBlocks.TALL_ALOE_VERA.get()) || !upper.is(lower.getBlock())
                    || lower.getValue(AloeVeraTallBlock.HALF) != DoubleBlockHalf.LOWER
                    || upper.getValue(AloeVeraTallBlock.HALF) != DoubleBlockHalf.UPPER)
                return false;
            int age = lower.getValue(AloeVeraTallBlock.AGE);
            if (upper.getValue(AloeVeraTallBlock.AGE) != age || (!context.harvestPartiallyGrown() && age != 8))
                return false;
            blossoms = age - 5;
        } else if (!lower.is(AtmosphericBlocks.ALOE_VERA.get()) || lower.getValue(AloeVeraBlock.AGE) != 5
                || context.level().getBlockState(root.below()).is(AtmosphericBlockTags.TALL_ALOE_GROWABLE_ON))
            return false;
        if (!context.claimHarvest(root))
            return false;
        if (HarvestOperations.dropsEnabled(context)) {
            context.collect(new ItemStack(AtmosphericItems.ALOE_LEAVES.get(), 3 + context.level().random.nextInt(5)));
            if (tall) {
                context.collect(new ItemStack(AtmosphericItems.YELLOW_BLOSSOMS.get(), blossoms));
                context.collect(new ItemStack(AtmosphericItems.ALOE_KERNELS.get()));
            }
        }
        if (tall)
            context.level().setBlock(root.above(), context.level().getFluidState(root.above()).createLegacyBlock(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        context.level().setBlockAndUpdate(root, AtmosphericBlocks.ALOE_VERA.get().defaultBlockState().setValue(AloeVeraBlock.AGE, 2));
        if (tall) {
            context.level().updateNeighborsAt(root.above(), lower.getBlock());
            context.level().getBlockState(root.above()).updateNeighbourShapes(context.level(), root.above(), Block.UPDATE_ALL);
        }
        context.level().playSound(null, root, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1,
                0.8F + context.level().random.nextFloat() * 0.4F);
        return true;
    }
}
