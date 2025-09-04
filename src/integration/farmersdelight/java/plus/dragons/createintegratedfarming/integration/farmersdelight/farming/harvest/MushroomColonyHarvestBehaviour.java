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

package plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

public class MushroomColonyHarvestBehaviour implements CustomHarvestBehaviour {
    private final MushroomColonyBlock colony;
    private final Block mushroom;

    protected MushroomColonyHarvestBehaviour(MushroomColonyBlock colony, Block mushroom) {
        this.colony = colony;
        this.mushroom = mushroom;
    }

    public static @Nullable MushroomColonyHarvestBehaviour create(Block block) {
        if (!(block instanceof MushroomColonyBlock colony))
            return null;
        if (!(colony.mushroomType.value() instanceof BlockItem mushroom))
            return null;
        return new MushroomColonyHarvestBehaviour(colony, mushroom.getBlock());
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        if (CIFConfig.server().mushroomColoniesDropSelf.get()) {
            harvestColony(behaviour, context, pos, state);
        } else {
            harvestMushroom(behaviour, context, pos, state);
        }
    }

    protected void harvestMushroom(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age == 0)
            return;
        if (age < colony.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        Level level = context.world;
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state.setValue(colony.getAgeProperty(), 0));
        behaviour.dropItem(context, new ItemStack(mushroom, age));
    }

    protected void harvestColony(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age < colony.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        BlockHelper.destroyBlockAs(
                context.world,
                pos,
                null,
                CustomHarvestBehaviour.getHarvestTool(context, new ItemStack(Items.SHEARS)),
                1,
                stack -> behaviour.dropItem(context, stack));
        replantMushroom(context, pos);
    }

    protected void replantMushroom(MovementContext context, BlockPos pos) {
        if (!CustomHarvestBehaviour.replant())
            return;
        Level level = context.world;
        BlockState newState = mushroom.defaultBlockState();
        if (!newState.canSurvive(level, pos))
            return;
        ItemStack available = ItemHelper.extract(
                context.contraption.getStorage().getAllItems(),
                stack -> stack.is(mushroom.asItem()),
                1,
                false);
        if (available.isEmpty())
            return;
        level.setBlockAndUpdate(pos, newState);
    }
}
