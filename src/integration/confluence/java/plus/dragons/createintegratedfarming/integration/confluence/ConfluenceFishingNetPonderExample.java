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

package plus.dragons.createintegratedfarming.integration.confluence;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.FoodItems;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class ConfluenceFishingNetPonderExample {
    private ConfluenceFishingNetPonderExample() {}

    public static void register() {
        FishingNetPonderExamples.register(new FishingNetPonderExample(
                ModIntegration.CONFLUENCE.asResource("obsidifish"),
                level -> {
                    var entity = new ItemEntity(level, 0.0, 0.0, 0.0, new ItemStack(FoodItems.OBSIDIFISH.get()));
                    entity.setNoGravity(true);
                    return entity;
                },
                () -> new ItemStack(FoodItems.OBSIDIFISH.get())));
    }
}
