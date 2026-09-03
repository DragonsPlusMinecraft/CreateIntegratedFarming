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

package plus.dragons.createintegratedfarming.integration.confluence.registry;

import java.util.List;
import net.minecraft.tags.ItemTags;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.ItemFeedSource;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfile;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.common.registry.CIFLootTables;

public final class ConfluenceRoostingDisplayProfiles {
    private ConfluenceRoostingDisplayProfiles() {}

    public static void register() {
        RoostingDisplayProfiles.register(new RoostingDisplayProfile(
                CIFCommon.asResource("roosting/confluence_duck"),
                ConfluenceBlocks.DUCK_ROOST_COMMON,
                List.of(ConfluenceBlocks.DUCK_ROOST_MALLARD),
                ItemFeedSource.itemTag(ItemTags.FISHES),
                CIFLootTables.CHICKEN_ROOST,
                AnimalRoostBlockEntity.DEFAULT_MINIMUM_PRODUCTION_TICKS,
                AnimalRoostBlockEntity.DEFAULT_MAXIMUM_PRODUCTION_TICKS));
    }
}
