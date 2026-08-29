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

package plus.dragons.createintegratedfarming.integration.starcatcher;

import com.wdiscute.starcatcher.registry.SCEntities;
import com.wdiscute.starcatcher.registry.SCItems;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class StarcatcherFishingNetPonderExample {
    private StarcatcherFishingNetPonderExample() {}

    public static void register() {
        FishingNetPonderExamples.register(new FishingNetPonderExample(
                ModIntegration.STARCATCHER.asResource("cerberay"),
                level -> {
                    var fish = SCEntities.FISH.get().create(level);
                    if (fish != null)
                        fish.setFish(new ItemStack(SCItems.CERBERAY.get()));
                    return fish;
                },
                () -> new ItemStack(SCItems.CERBERAY.get())));
    }
}
