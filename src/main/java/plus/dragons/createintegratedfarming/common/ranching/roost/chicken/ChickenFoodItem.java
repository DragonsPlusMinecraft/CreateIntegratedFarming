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

package plus.dragons.createintegratedfarming.common.ranching.roost.chicken;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;

public record ChickenFoodItem(IntProvider progress, IntProvider cooldown, Optional<ItemStack> usingConvertsTo) implements ChickenFood {

    public static final Codec<ChickenFoodItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.codec(0, 12000).fieldOf("progress").forGetter(ChickenFoodItem::progress),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("cooldown").forGetter(ChickenFoodItem::cooldown),
            ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("using_converts_to").forGetter(ChickenFoodItem::usingConvertsTo)).apply(instance, ChickenFoodItem::new));
    public int getProgress(RandomSource random) {
        return progress.sample(random);
    }

    public int getCooldown(RandomSource random) {
        return cooldown.sample(random);
    }
}
