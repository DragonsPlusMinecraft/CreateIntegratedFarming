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

package plus.dragons.createintegratedfarming.integration.untitledduck.registry;

import java.util.List;
import net.untitledduckmod.common.init.ModTags;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.ItemFeedSource;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfile;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayProfiles;

public final class UntitledDuckRoostingDisplayProfiles {
    private UntitledDuckRoostingDisplayProfiles() {}

    public static void register() {
        RoostingDisplayProfiles.register(new RoostingDisplayProfile(
                CIFCommon.asResource("roosting/untitled_duck"),
                UntitledDuckBlocks.DUCK_ROOST_NORMAL,
                List.of(
                        UntitledDuckBlocks.DUCK_ROOST_FEMALE,
                        UntitledDuckBlocks.DUCK_ROOST_CAMPBELL,
                        UntitledDuckBlocks.DUCK_ROOST_PEKIN),
                ItemFeedSource.itemTag(ModTags.ItemTags.DUCK_BREEDING_FOOD),
                UntitledDuckLootTables.DUCK_ROOST,
                AnimalRoostBlockEntity.DEFAULT_MINIMUM_PRODUCTION_TICKS,
                AnimalRoostBlockEntity.DEFAULT_MAXIMUM_PRODUCTION_TICKS));
        RoostingDisplayProfiles.register(new RoostingDisplayProfile(
                CIFCommon.asResource("roosting/untitled_goose"),
                UntitledDuckBlocks.GOOSE_ROOST_NORMAL,
                List.of(
                        UntitledDuckBlocks.GOOSE_ROOST_CANADIAN,
                        UntitledDuckBlocks.GOOSE_ROOST_GREYLAG,
                        UntitledDuckBlocks.GOOSE_ROOST_PING,
                        UntitledDuckBlocks.GOOSE_ROOST_SUS,
                        UntitledDuckBlocks.GOOSE_ROOST_UNTITLED),
                ItemFeedSource.itemTag(ModTags.ItemTags.GOOSE_BREEDING_FOOD),
                UntitledDuckLootTables.GOOSE_ROOST,
                AnimalRoostBlockEntity.DEFAULT_MINIMUM_PRODUCTION_TICKS,
                AnimalRoostBlockEntity.DEFAULT_MAXIMUM_PRODUCTION_TICKS));
    }
}
