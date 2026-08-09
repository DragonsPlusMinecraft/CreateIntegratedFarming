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

package plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.duck;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.untitledduckmod.common.init.ModSoundEvents;
import net.untitledduckmod.common.init.ModTags;
import plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.UntitledAnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckLootTables;

public class DuckRoostBlockEntity extends UntitledAnimalRoostBlockEntity {
    public DuckRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected Predicate<ItemStack> getFoodPredicate() {
        return stack -> stack.is(ModTags.ItemTags.DUCK_BREEDING_FOOD);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.DUCK_AMBIENT.get();
    }

    @Override
    protected SoundEvent productionSound() {
        return ModSoundEvents.DUCK_LAY_EGG.get();
    }

    @Override
    protected ResourceKey<LootTable> productionLootTable() {
        return UntitledDuckLootTables.DUCK_ROOST;
    }
}
