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

package plus.dragons.createintegratedfarming.integration.autumnity.farming.harvest;

import com.teamabnormals.autumnity.common.block.TallFoulBerryBushBlock;
import com.teamabnormals.autumnity.core.registry.AutumnityBlocks;
import com.teamabnormals.autumnity.core.registry.AutumnityItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class FoulBerryHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof TallFoulBerryBushBlock))
            return false;
        BlockPos root = state.getValue(TallFoulBerryBushBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        if (!HarvestOperations.canHarvest(context, List.of(root, root.above())))
            return false;
        BlockState lower = context.level().getBlockState(root);
        BlockState upper = context.level().getBlockState(root.above());
        if (!lower.is(AutumnityBlocks.TALL_FOUL_BERRY_BUSH.get()) || !upper.is(lower.getBlock())
                || lower.getValue(TallFoulBerryBushBlock.HALF) != DoubleBlockHalf.LOWER
                || upper.getValue(TallFoulBerryBushBlock.HALF) != DoubleBlockHalf.UPPER)
            return false;
        int age = lower.getValue(TallFoulBerryBushBlock.AGE);
        if (age < 2 || upper.getValue(TallFoulBerryBushBlock.AGE) != age || !context.claimHarvest(root))
            return false;
        if (HarvestOperations.dropsEnabled(context))
            context.collect(new ItemStack(AutumnityItems.FOUL_BERRIES.get(), 2));
        context.level().setBlock(root, lower.setValue(TallFoulBerryBushBlock.AGE, age - 1), 2);
        context.level().setBlock(root.above(), upper.setValue(TallFoulBerryBushBlock.AGE, age - 1), 2);
        context.level().playSound(null, root, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1,
                0.8F + context.level().random.nextFloat() * 0.4F);
        return true;
    }
}
