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

package plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest;

import alabaster.hearthandharvest.common.block.IHarvestable;
import alabaster.hearthandharvest.common.block.trellis.TrellisBlock;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class TrellisHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    private final TrellisBlock trellis;
    private final IntegerProperty age;

    private TrellisHarvestBehaviour(TrellisBlock trellis, IntegerProperty age) {
        this.trellis = trellis;
        this.age = age;
    }

    public static @Nullable TrellisHarvestBehaviour create(Block block) {
        // The 1.3 grape trellis and 1.4 crop trellis each define their own age property.
        if (block instanceof TrellisBlock trellis && block instanceof IHarvestable
                && block.getStateDefinition().getProperty("age") instanceof IntegerProperty age)
            return new TrellisHarvestBehaviour(trellis, age);
        return null;
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        var level = context.level();
        BlockState current = level.getBlockState(pos);
        if (!current.is(trellis) || current.getValue(age) != 4)
            return false;
        ItemStack produce = switch (trellis.getPlant(current)) {
            case RED_GRAPE -> new ItemStack(HHModItems.RED_GRAPES.get(), 1 + level.random.nextInt(2));
            case GREEN_GRAPE -> new ItemStack(HHModItems.GREEN_GRAPES.get(), 1 + level.random.nextInt(2));
            case HOPS -> new ItemStack(HHModItems.HOPS.get(), 2 + level.random.nextInt(3));
            default -> ItemStack.EMPTY;
        };
        if (produce.isEmpty() || !HarvestOperations.pickFruit(context, pos, current.setValue(age, 0), produce))
            return false;
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        return true;
    }
}
