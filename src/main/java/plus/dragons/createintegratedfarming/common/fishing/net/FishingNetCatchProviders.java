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

package plus.dragons.createintegratedfarming.common.fishing.net;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class FishingNetCatchProviders {
    private static final Map<FishingNetMedium, LinkedHashMap<ResourceLocation, FishingNetCatchProvider>> PROVIDERS = new EnumMap<>(FishingNetMedium.class);

    static {
        for (FishingNetMedium medium : FishingNetMedium.values())
            PROVIDERS.put(medium, new LinkedHashMap<>());
    }

    private FishingNetCatchProviders() {}

    public static synchronized void register(
            ResourceLocation id, FishingNetMedium medium, FishingNetCatchProvider provider) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(medium, "medium");
        Objects.requireNonNull(provider, "provider");
        if (PROVIDERS.get(medium).putIfAbsent(id, provider) != null)
            throw new IllegalArgumentException("Duplicate fishing net catch provider: " + id + " for " + medium);
    }

    public static List<ItemStack> getCatch(FishingNetCatchContext context) {
        List<FishingNetCatchProvider> providers;
        synchronized (FishingNetCatchProviders.class) {
            providers = List.copyOf(PROVIDERS.get(context.medium()).values());
        }
        if (providers.isEmpty())
            return List.of();
        return providers.get(context.random().nextInt(providers.size())).getCatch(context);
    }
}
