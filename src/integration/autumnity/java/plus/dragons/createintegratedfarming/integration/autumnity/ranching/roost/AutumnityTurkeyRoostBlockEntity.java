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

package plus.dragons.createintegratedfarming.integration.autumnity.ranching.roost;

import com.teamabnormals.autumnity.core.other.tags.AutumnityItemTags;
import com.teamabnormals.autumnity.core.registry.AutumnitySoundEvents;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import plus.dragons.createintegratedfarming.common.ranching.roost.TaggedAnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.integration.autumnity.registry.AutumnityLootTables;

public class AutumnityTurkeyRoostBlockEntity extends TaggedAnimalRoostBlockEntity {
    public static final int MINIMUM_PRODUCTION_TICKS = 9600;
    public static final int MAXIMUM_PRODUCTION_TICKS = 19199;

    public AutumnityTurkeyRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int minimumProductionTicks() {
        return MINIMUM_PRODUCTION_TICKS;
    }

    @Override
    protected int maximumProductionTicks() {
        return MAXIMUM_PRODUCTION_TICKS;
    }

    @Override
    protected Predicate<ItemStack> getFoodPredicate() {
        return stack -> stack.is(AutumnityItemTags.TURKEY_FOOD);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return AutumnitySoundEvents.ENTITY_TURKEY_AMBIENT.get();
    }

    @Override
    protected SoundEvent productionSound() {
        return AutumnitySoundEvents.ENTITY_TURKEY_EGG.get();
    }

    @Override
    protected ResourceKey<LootTable> productionLootTable() {
        return AutumnityLootTables.TURKEY_ROOST;
    }
}
