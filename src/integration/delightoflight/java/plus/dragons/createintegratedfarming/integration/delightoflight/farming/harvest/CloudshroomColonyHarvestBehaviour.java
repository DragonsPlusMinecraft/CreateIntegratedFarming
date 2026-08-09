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

package plus.dragons.createintegratedfarming.integration.delightoflight.farming.harvest;

import static com.cloudmeow.delightoflight.block.CloudshroomColonyBlock.WEATHER_AGE;

import com.cloudmeow.delightoflight.block.CloudshroomColonyBlock;
import com.cloudmeow.delightoflight.registry.DFItems;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class CloudshroomColonyHarvestBehaviour implements CustomHarvestBehaviour {
    CloudshroomColonyBlock colony;

    public CloudshroomColonyHarvestBehaviour(CloudshroomColonyBlock colony) {
        this.colony = colony;
    }

    public static @Nullable CloudshroomColonyHarvestBehaviour create(Block block) {
        if (!(block instanceof CloudshroomColonyBlock colony))
            return null;
        return new CloudshroomColonyHarvestBehaviour(colony);
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age == 0)
            return;
        if (age < colony.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        Level level = context.world;
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state.setValue(colony.getAgeProperty(), 0));
        int weather = state.getValue(WEATHER_AGE);
        var mushroom = weather == 2 ? DFItems.THUNDER_CLOUDSHROOM.get() : weather == 1 ? DFItems.RAINY_CLOUDSHROOM.get() : DFItems.CLEAR_CLOUDSHROOM.get();
        behaviour.collectOrDropItem(context, new ItemStack(mushroom, age));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age == 0 || age < colony.getMaxAge() && !context.harvestPartiallyGrown())
            return false;
        Level level = context.level();
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state.setValue(colony.getAgeProperty(), 0));
        int weather = state.getValue(WEATHER_AGE);
        var mushroom = weather == 2
                ? DFItems.THUNDER_CLOUDSHROOM.get()
                : weather == 1 ? DFItems.RAINY_CLOUDSHROOM.get() : DFItems.CLEAR_CLOUDSHROOM.get();
        context.collect(new ItemStack(mushroom, age));
        return true;
    }
}
