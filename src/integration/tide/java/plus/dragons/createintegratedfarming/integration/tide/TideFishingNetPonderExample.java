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

package plus.dragons.createintegratedfarming.integration.tide;

import com.li64.tide.registries.TideEntityTypes;
import com.li64.tide.registries.TideFish;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class TideFishingNetPonderExample {
    private TideFishingNetPonderExample() {}

    public static void register() {
        FishingNetPonderExamples.register(new FishingNetPonderExample(
                ModIntegration.TIDE.asResource("ash_perch"),
                level -> {
                    var entityType = TideEntityTypes.ENTITY_TYPES.get("ash_perch");
                    return entityType == null ? null : entityType.create(level);
                },
                () -> new ItemStack(TideFish.ASH_PERCH)));
    }
}
