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

package plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;

public abstract class UntitledAnimalRoostBlockEntity extends AnimalRoostBlockEntity {
    private static final IntProvider FOOD_PROGRESSION = ConstantInt.of(2400);
    private static final IntProvider FOOD_COOLDOWN = UniformInt.of(400, 800);

    public UntitledAnimalRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract Predicate<ItemStack> getFoodPredicate();

    public abstract SoundEvent getAmbientSound();

    @Override
    public boolean feedItem(ItemStack stack, boolean simulate) {
        assert level != null;
        if (feedCooldown > 0 || eggTime <= 0)
            return false;
        if (getFoodPredicate().test(stack)) {
            if (simulate)
                return true;
            Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            Vec3 feedPos = Vec3.atBottomCenterOf(worldPosition)
                    .add(facing.getStepX() * .5f, 13 / 16f, facing.getStepZ() * .5f);
            level.addParticle(
                    new ItemParticleOption(ParticleTypes.ITEM, stack),
                    feedPos.x, feedPos.y, feedPos.z,
                    0, 0, 0);
            eggTime = Math.max(0, eggTime - FOOD_PROGRESSION.sample(level.random));
            feedCooldown = FOOD_COOLDOWN.sample(level.random);
            level.playSound(
                    null, worldPosition, getAmbientSound(), SoundSource.BLOCKS,
                    1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
            notifyUpdate();
            var remainer = stack.getCraftingRemainingItem();
            if (!remainer.isEmpty())
                Containers.dropItemStack(level, feedPos.x, feedPos.y, feedPos.z, remainer.copy());
            return true;
        }
        return false;
    }
}
