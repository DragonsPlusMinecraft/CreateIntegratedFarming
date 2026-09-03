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

package plus.dragons.createintegratedfarming.common.ranching.roost;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TaggedAnimalRoostBlockEntity extends AnimalRoostBlockEntity {
    public static final int FOOD_PROGRESSION = 2400;
    public static final int MINIMUM_FOOD_COOLDOWN = 400;
    public static final int MAXIMUM_FOOD_COOLDOWN = 800;
    private static final IntProvider FOOD_PROGRESSION_PROVIDER = ConstantInt.of(FOOD_PROGRESSION);
    private static final IntProvider FOOD_COOLDOWN_PROVIDER = UniformInt.of(
            MINIMUM_FOOD_COOLDOWN, MAXIMUM_FOOD_COOLDOWN);

    protected TaggedAnimalRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract Predicate<ItemStack> getFoodPredicate();

    protected abstract SoundEvent getAmbientSound();

    @Override
    protected SoundEvent feedingSound() {
        return getAmbientSound();
    }

    @Override
    public boolean feedItem(ItemStack stack, boolean simulate) {
        assert level != null;
        if (feedCooldown > 0 || eggTime <= 0 || !getFoodPredicate().test(stack))
            return false;
        if (simulate)
            return true;
        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        Vec3 feedPos = Vec3.atBottomCenterOf(worldPosition)
                .add(facing.getStepX() * .5f, 13 / 16f, facing.getStepZ() * .5f);
        level.addParticle(
                new ItemParticleOption(ParticleTypes.ITEM, stack),
                feedPos.x, feedPos.y, feedPos.z,
                0, 0, 0);
        applyFeeding(
                FOOD_PROGRESSION_PROVIDER.sample(level.random),
                FOOD_COOLDOWN_PROVIDER.sample(level.random));
        ItemStack remainder = stack.getCraftingRemainingItem();
        if (!remainder.isEmpty())
            Containers.dropItemStack(level, feedPos.x, feedPos.y, feedPos.z, remainder.copy());
        return true;
    }
}
