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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import vectorwing.farmersdelight.common.block.HangingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class TomatoHarvestBehaviour implements CustomHarvestBehaviour {
    private final TomatoBlock tomato;

    public TomatoHarvestBehaviour(TomatoBlock tomato) {
        this.tomato = tomato;
    }

    public static @Nullable TomatoHarvestBehaviour create(Block block) {
        if (!(block instanceof TomatoBlock tomato))
            return null;
        return new TomatoHarvestBehaviour(tomato);
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
            level.playSound(null, pos, ModSounds.BLOCK_TOMATOES_PICK_TOMATOES.get(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        } else if (partial) {
            level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        }
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        boolean mature = tomato.getAge(state) == tomato.getMaxAge();
        if (!mature && !context.harvestPartiallyGrown())
            return false;
        Level level = context.level();
        if (!context.replant()) {
            breakTomatoes(context, pos, state);
            return true;
        }
        if (mature) {
            context.collect(new ItemStack(ModItems.TOMATO.get(), 1 + level.random.nextInt(2)));
            if (level.random.nextFloat() < 0.05F)
                context.collect(new ItemStack(ModItems.ROTTEN_TOMATO.get()));
            level.playSound(
                    null, pos, ModSounds.BLOCK_TOMATOES_PICK_TOMATOES.get(), SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        return true;
    }

    protected void breakTomatoes(AreaHarvestContext context, BlockPos pos, BlockState state) {
        Level level = context.level();
        BlockPos above = pos.above();
        BlockState stateAbove = level.getBlockState(above);
        if (isTomatoCrop(stateAbove))
            breakTomatoes(context, above, stateAbove);
        boolean restoreRope = shouldRestoreRope(state);
        CustomHarvestBehaviour.harvestBlock(
                level, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), null,
                context.tool(), 1.0F, context::collect);
        if (restoreRope)
            restoreRope(level, pos, state);
    }

    protected void breakTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        BlockPos above = pos.above();
        BlockState stateAbove = level.getBlockState(above);
        if (isTomatoCrop(stateAbove))
            breakTomatoes(level, behaviour, context, above, stateAbove);
        boolean restoreRope = shouldRestoreRope(state);
        BlockHelper.destroyBlockAs(
                level,
                pos,
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1,
                stack -> behaviour.collectOrDropItem(context, stack));
        if (restoreRope)
            restoreRope(level, pos, state);
    }

    protected void dropTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context) {
        behaviour.collectOrDropItem(context, new ItemStack(ModItems.TOMATO.get(), 1 + level.random.nextInt(2)));
        if (level.random.nextFloat() < 0.05)
            behaviour.collectOrDropItem(context, new ItemStack(ModItems.ROTTEN_TOMATO.get()));
    }

    protected boolean isTomatoCrop(BlockState state) {
        return state.getBlock() instanceof TomatoBlock;
    }

    protected boolean shouldRestoreRope(BlockState state) {
        if (state.getBlock() instanceof HangingTomatoBlock)
            return true;
        return state.hasProperty(TomatoBlock.ROPELOGGED) && state.getValue(TomatoBlock.ROPELOGGED);
    }

    protected void restoreRope(Level level, BlockPos pos, BlockState state) {
        HangingTomatoBlock ropePlacer = state.getBlock() instanceof HangingTomatoBlock hangingTomato
                ? hangingTomato
                : ModBlocks.TOMATO_CROP_ON_ROPE.get();
        ropePlacer.placeRope(level, pos);
    }
}
