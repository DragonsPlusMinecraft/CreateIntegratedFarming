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

import com.teamabnormals.neapolitan.common.block.MintBlock;
import com.teamabnormals.neapolitan.core.registry.NeapolitanItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class MintHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof MintBlock) || state.getValue(MintBlock.AGE) != 4
                || !HarvestOperations.canHarvest(context, List.of(pos)) || !context.claimHarvest(pos))
            return false;
        if (HarvestOperations.dropsEnabled(context))
            context.collect(new ItemStack(NeapolitanItems.MINT_LEAVES.get(), state.getValue(MintBlock.SPROUTS)));
        context.level().setBlock(pos, state.setValue(MintBlock.AGE, 1), 2);
        context.level().playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1,
                0.8F + context.level().random.nextFloat() * 0.4F);
        return true;
    }
}
