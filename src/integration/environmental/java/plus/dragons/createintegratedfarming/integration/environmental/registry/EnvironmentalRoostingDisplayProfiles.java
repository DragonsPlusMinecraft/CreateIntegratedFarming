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

package plus.dragons.createintegratedfarming.integration.environmental.registry;

import com.teamabnormals.environmental.core.other.tags.EnvironmentalItemTags;
import java.util.List;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.ItemFeedSource;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfile;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfiles;

public final class EnvironmentalRoostingDisplayProfiles {
    private EnvironmentalRoostingDisplayProfiles() {}

    public static void register() {
        RoostingDisplayProfiles.register(new RoostingDisplayProfile(
                CIFCommon.asResource("roosting/environmental_duck"),
                EnvironmentalBlocks.DUCK_ROOST,
                List.of(),
                ItemFeedSource.itemTag(EnvironmentalItemTags.DUCK_FOOD),
                EnvironmentalLootTables.DUCK_ROOST,
                AnimalRoostBlockEntity.DEFAULT_MINIMUM_PRODUCTION_TICKS,
                AnimalRoostBlockEntity.DEFAULT_MAXIMUM_PRODUCTION_TICKS));
    }
}
