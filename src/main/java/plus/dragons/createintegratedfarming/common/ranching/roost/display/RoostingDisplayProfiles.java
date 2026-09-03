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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlock;

@ApiStatus.Internal
public final class RoostingDisplayProfiles {
    private static final Map<ResourceLocation, RoostingDisplayProfile> BY_ID = new LinkedHashMap<>();
    private static final Map<Block, ResourceLocation> BY_BLOCK = new IdentityHashMap<>();

    private RoostingDisplayProfiles() {}

    public static synchronized void register(RoostingDisplayProfile profile) {
        if (BY_ID.containsKey(profile.id()))
            throw new IllegalArgumentException("Duplicate roosting display profile: " + profile.id());

        Block representative = profile.representativeRoost().get();
        if (!(representative instanceof RoostBlock))
            throw new IllegalArgumentException("Representative is not a roost block: " + profile.id());

        var blocks = new ArrayList<Block>();
        blocks.add(representative);
        profile.equivalentRoosts().forEach(supplier -> {
            Block block = supplier.get();
            if (!(block instanceof RoostBlock))
                throw new IllegalArgumentException("Profile contains a non-roost block: " + profile.id());
            if (!blocks.contains(block))
                blocks.add(block);
        });

        for (Block block : blocks) {
            ResourceLocation existing = BY_BLOCK.putIfAbsent(block, profile.id());
            if (existing != null)
                throw new IllegalArgumentException(
                        "Roost block belongs to both " + existing + " and " + profile.id());
        }
        BY_ID.put(profile.id(), profile);
    }

    public static synchronized List<RoostingDisplayProfile> all() {
        return BY_ID.values().stream()
                .sorted(Comparator.comparing(profile -> profile.id().toString()))
                .toList();
    }
}
