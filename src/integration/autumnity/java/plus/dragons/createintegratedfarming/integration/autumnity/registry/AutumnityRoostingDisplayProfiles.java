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

package plus.dragons.createintegratedfarming.integration.autumnity.registry;

import com.teamabnormals.autumnity.core.other.tags.AutumnityItemTags;
import java.util.List;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.ItemFeedSource;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfile;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.integration.autumnity.ranching.roost.AutumnityTurkeyRoostBlockEntity;

public final class AutumnityRoostingDisplayProfiles {
    private AutumnityRoostingDisplayProfiles() {}

    public static void register() {
        RoostingDisplayProfiles.register(new RoostingDisplayProfile(
                CIFCommon.asResource("roosting/autumnity_turkey"),
                AutumnityBlocks.TURKEY_ROOST,
                List.of(),
                ItemFeedSource.itemTag(AutumnityItemTags.TURKEY_FOOD),
                AutumnityLootTables.TURKEY_ROOST,
                AutumnityTurkeyRoostBlockEntity.MINIMUM_PRODUCTION_TICKS,
                AutumnityTurkeyRoostBlockEntity.MAXIMUM_PRODUCTION_TICKS));
    }
}
