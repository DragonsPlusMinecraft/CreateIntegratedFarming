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

package plus.dragons.createintegratedfarming.common.ranching.roost.display;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record RoostingDisplayProfile(
        ResourceLocation id,
        Supplier<? extends Block> representativeRoost,
        List<Supplier<? extends Block>> equivalentRoosts,
        ItemFeedSource itemFeedSource,
        ResourceKey<LootTable> productionLootTable,
        int minimumProductionTicks,
        int maximumProductionTicks) {
    public RoostingDisplayProfile {
        equivalentRoosts = List.copyOf(equivalentRoosts);
        if (minimumProductionTicks < 0 || maximumProductionTicks < minimumProductionTicks)
            throw new IllegalArgumentException("Invalid production time for roosting profile " + id);
    }
}
