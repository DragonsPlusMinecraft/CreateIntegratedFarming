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

package plus.dragons.createintegratedfarming.common.ranching.roost.chicken;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.registry.CIFDataMaps;
import plus.dragons.createintegratedfarming.common.registry.CIFLootTables;

public class ChickenRoostBlockEntity extends AnimalRoostBlockEntity {
    @Override
    protected ResourceKey<LootTable> productionLootTable() {
        return CIFLootTables.CHICKEN_ROOST;
    }

    public ChickenRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean feedItem(ItemStack stack, boolean simulate) {
        assert level != null;
        if (feedCooldown > 0 || eggTime <= 0)
            return false;
        var food = stack.getItemHolder().getData(CIFDataMaps.CHICKEN_FOOD_ITEMS);
        if (food == null)
            return false;
        if (simulate)
            return true;
        feed(food);
        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        Vec3 feedPos = Vec3.atBottomCenterOf(worldPosition)
                .add(facing.getStepX() * .5f, 13 / 16f, facing.getStepZ() * .5f);
        food.usingConvertsTo().ifPresent(remainer -> Containers.dropItemStack(
                level, feedPos.x, feedPos.y, feedPos.z, remainer));
        level.addParticle(
                new ItemParticleOption(ParticleTypes.ITEM, stack),
                feedPos.x, feedPos.y, feedPos.z,
                0, 0, 0);
        return true;
    }
}
