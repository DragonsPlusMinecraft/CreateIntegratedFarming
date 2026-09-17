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

package plus.dragons.createintegratedfarming.integration.mynethersdelight.farming.harvest;

import com.soytutta.mynethersdelight.common.block.crops.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCannonBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyFlowerBlock;
import com.soytutta.mynethersdelight.common.block.utility.MNDBlockStateProperties;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class PowderyHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static boolean supports(Block block) {
        return block instanceof PowderyCaneBlock || block instanceof PowderyCannonBlock || block instanceof PowderyFlowerBlock;
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!supports(state.getBlock()) || !state.getValue(BlockStateProperties.LIT)
                || !HarvestOperations.canHarvest(context, List.of(pos)))
            return false;
        boolean cannon = state.getBlock() instanceof PowderyCannonBlock;
        int age = cannon ? 0 : state.getValue(BlockStateProperties.AGE_3);
        if ((!cannon && age <= 1) || !context.claimHarvest(pos))
            return false;
        int amount = cannon ? 3 + context.level().random.nextInt(6)
                : 1 + context.level().random.nextInt(2) + (age == 3 ? 1 : 0);
        if (HarvestOperations.dropsEnabled(context))
            context.collect(new ItemStack(MNDItems.BULLET_PEPPER.get(), amount));
        BlockState reset = state.setValue(BlockStateProperties.LIT, false);
        if (!cannon)
            reset = reset.setValue(BlockStateProperties.AGE_3, 0).setValue(MNDBlockStateProperties.PRESSURE, 0);
        context.level().setBlockAndUpdate(pos, reset);
        context.level().playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1,
                0.8F + context.level().random.nextFloat() * 0.4F);
        return true;
    }
}
