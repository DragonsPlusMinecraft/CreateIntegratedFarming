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

package plus.dragons.createintegratedfarming.common.network;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySnapshot;

/** Client-side state with no dependency on JEI, so packets may arrive before JEI is initialized. */
@ApiStatus.Internal
public final class RoostingDisplayClientCache {
    private static final CopyOnWriteArrayList<Consumer<RoostingDisplaySnapshot>> LISTENERS = new CopyOnWriteArrayList<>();
    private static final Set<ResourceLocation> WARNED_IDS = ConcurrentHashMap.newKeySet();
    private static volatile RoostingDisplaySnapshot snapshot = RoostingDisplaySnapshot.EMPTY;

    private RoostingDisplayClientCache() {}

    public static RoostingDisplaySnapshot get() {
        return snapshot;
    }

    public static synchronized void accept(RoostingDisplaySnapshot received) {
        if (received.revision() <= snapshot.revision())
            return;
        var validRecipes = received.recipes().stream()
                .filter(RoostingDisplayClientCache::hasRepresentative)
                .map(RoostingDisplayClientCache::removeUnknownEquivalents)
                .toList();
        publish(new RoostingDisplaySnapshot(received.revision(), validRecipes));
    }

    public static synchronized void clear() {
        WARNED_IDS.clear();
        publish(RoostingDisplaySnapshot.EMPTY);
    }

    public static Runnable addListener(Consumer<RoostingDisplaySnapshot> listener) {
        LISTENERS.add(listener);
        return () -> LISTENERS.remove(listener);
    }

    private static void publish(RoostingDisplaySnapshot updated) {
        snapshot = updated;
        LISTENERS.forEach(listener -> listener.accept(updated));
    }

    private static boolean hasRepresentative(RoostingDisplayRecipe recipe) {
        if (BuiltInRegistries.BLOCK.containsKey(recipe.representativeBlock()))
            return true;
        warnUnknown(recipe.representativeBlock());
        return false;
    }

    private static RoostingDisplayRecipe removeUnknownEquivalents(RoostingDisplayRecipe recipe) {
        var equivalents = recipe.equivalentBlocks().stream()
                .filter(id -> {
                    boolean known = BuiltInRegistries.BLOCK.containsKey(id);
                    if (!known)
                        warnUnknown(id);
                    return known;
                })
                .toList();
        return equivalents.size() == recipe.equivalentBlocks().size()
                ? recipe
                : new RoostingDisplayRecipe(
                        recipe.id(),
                        recipe.representativeBlock(),
                        equivalents,
                        recipe.productionTime(),
                        recipe.itemFeeds(),
                        recipe.fluidFeeds(),
                        recipe.outputs(),
                        recipe.lootStatus());
    }

    private static void warnUnknown(ResourceLocation id) {
        if (WARNED_IDS.add(id))
            CIFCommon.LOGGER.warn("Ignoring unknown block in roosting display snapshot: {}", id);
    }
}
