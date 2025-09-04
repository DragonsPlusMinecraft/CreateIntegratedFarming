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
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class TomatoHarvestBehaviour implements CustomHarvestBehaviour {
    private final TomatoVineBlock tomato;

    public TomatoHarvestBehaviour(TomatoVineBlock tomato) {
        this.tomato = tomato;
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        boolean replant = CustomHarvestBehaviour.replant();
        boolean partial = CustomHarvestBehaviour.partial();
        boolean mature = tomato.getAge(state) == tomato.getMaxAge();
        Level level = context.world;
        if (!replant) {
            if (mature || partial)
                breakTomatoes(level, behaviour, context, pos, state);
            return;
        }
        if (mature) {
            dropTomatoes(level, behaviour, context);
            level.playSound(null, pos, ModSounds.ITEM_TOMATO_PICK_FROM_BUSH.get(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        } else if (partial) {
            level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        }
    }

    protected void breakTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockPos above = pos.above();
        BlockState stateAbove = level.getBlockState(above);
        if (stateAbove.is(tomato))
            breakTomatoes(level, behaviour, context, above, stateAbove);
        boolean ropelogged = state.getValue(TomatoVineBlock.ROPELOGGED);
        BlockHelper.destroyBlockAs(
                level,
                pos,
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1,
                stack -> behaviour.dropItem(context, stack));
        if (ropelogged)
            level.setBlockAndUpdate(pos, getRope());
    }

    protected void dropTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context) {
        behaviour.dropItem(context, new ItemStack(ModItems.TOMATO.get(), 1 + level.random.nextInt(2)));
        if (level.random.nextFloat() < 0.05)
            behaviour.dropItem(context, new ItemStack(ModItems.ROTTEN_TOMATO.get()));
    }

    protected BlockState getRope() {
        ResourceLocation ropeId = ResourceLocation.parse(Configuration.DEFAULT_TOMATO_VINE_ROPE.get());
        BlockState rope = BuiltInRegistries.BLOCK.get(ropeId).defaultBlockState();
        if (rope.isAir())
            return ModBlocks.ROPE.get().defaultBlockState();
        return rope;
    }
}
