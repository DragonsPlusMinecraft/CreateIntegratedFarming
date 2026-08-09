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

package plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.mcreator.nethersexoticism.block.JaboticabaBranchBlock;
import net.mcreator.nethersexoticism.block.JaboticabaBranchEmptyBlock;
import net.mcreator.nethersexoticism.init.NethersExoticismModBlocks;
import net.mcreator.nethersexoticism.init.NethersExoticismModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class JaboticabaHarvestBehaviour implements CustomHarvestBehaviour {
    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockState fruiting = getFruitingState(context.world, pos);
        if (fruiting == null)
            return;
        behaviour.collectOrDropItem(context, createDrop(context.world));
        reset(context.world, pos, fruiting);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState fruiting = getFruitingState(context.level(), pos);
        if (fruiting == null)
            return false;
        context.collect(createDrop(context.level()));
        reset(context.level(), pos, fruiting);
        return true;
    }

    private static @Nullable BlockState getFruitingState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(NethersExoticismModBlocks.JABOTICABA_BRANCH.get()) ? state : null;
    }

    private static ItemStack createDrop(Level level) {
        int count = level.random.nextFloat() < 0.75F ? 1 : 2;
        return new ItemStack(NethersExoticismModItems.JABOTICABA.get(), count);
    }

    private static void reset(Level level, BlockPos pos, BlockState state) {
        BlockState empty = NethersExoticismModBlocks.JABOTICABA_BRANCH_EMPTY.get().defaultBlockState()
                .setValue(JaboticabaBranchEmptyBlock.AXIS, state.getValue(JaboticabaBranchBlock.AXIS))
                .setValue(JaboticabaBranchEmptyBlock.WATERLOGGED, state.getValue(JaboticabaBranchBlock.WATERLOGGED));
        level.setBlock(pos, empty, 3);
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
