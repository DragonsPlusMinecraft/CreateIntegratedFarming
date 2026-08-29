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

package plus.dragons.createintegratedfarming.integration.vanillabackport.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import plus.dragons.createintegratedfarming.common.CIFCommon;

public final class VanillaBackportLootTables {
    public static final ResourceKey<LootTable> WARM_CHICKEN_ROOST = ResourceKey.create(
            Registries.LOOT_TABLE, CIFCommon.asResource("gameplay/roost/vanillabackport_chicken_warm"));
    public static final ResourceKey<LootTable> COLD_CHICKEN_ROOST = ResourceKey.create(
            Registries.LOOT_TABLE, CIFCommon.asResource("gameplay/roost/vanillabackport_chicken_cold"));

    private VanillaBackportLootTables() {}
}
