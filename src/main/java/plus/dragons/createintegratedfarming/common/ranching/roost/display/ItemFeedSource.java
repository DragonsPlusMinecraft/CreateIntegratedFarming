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

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.ranching.roost.TaggedAnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.IntRange;

@ApiStatus.Internal
public sealed interface ItemFeedSource permits ItemFeedSource.ChickenDataMap, ItemFeedSource.ItemTag {
    ChickenDataMap CHICKEN_DATA_MAP = new ChickenDataMap();

    static ItemFeedSource chickenDataMap() {
        return CHICKEN_DATA_MAP;
    }

    static ItemFeedSource itemTag(TagKey<Item> tag) {
        return new ItemTag(
                tag,
                IntRange.exact(TaggedAnimalRoostBlockEntity.FOOD_PROGRESSION),
                new IntRange(
                        TaggedAnimalRoostBlockEntity.MINIMUM_FOOD_COOLDOWN,
                        TaggedAnimalRoostBlockEntity.MAXIMUM_FOOD_COOLDOWN));
    }

    record ChickenDataMap() implements ItemFeedSource {}

    record ItemTag(TagKey<Item> tag, IntRange progress, IntRange cooldown) implements ItemFeedSource {}
}
