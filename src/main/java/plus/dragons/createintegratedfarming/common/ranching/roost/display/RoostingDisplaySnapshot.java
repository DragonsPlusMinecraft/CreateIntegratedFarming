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
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RoostingDisplaySnapshot(int revision, List<RoostingDisplayRecipe> recipes) {
    public static final RoostingDisplaySnapshot EMPTY = new RoostingDisplaySnapshot(-1, List.of());
    public static final StreamCodec<RegistryFriendlyByteBuf, RoostingDisplaySnapshot> STREAM_CODEC = StreamCodec.ofMember(
            RoostingDisplaySnapshot::encode,
            RoostingDisplaySnapshot::decode);

    public RoostingDisplaySnapshot {
        recipes = List.copyOf(recipes);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(revision);
        buffer.writeVarInt(recipes.size());
        recipes.forEach(recipe -> RoostingDisplayRecipe.STREAM_CODEC.encode(buffer, recipe));
    }

    private static RoostingDisplaySnapshot decode(RegistryFriendlyByteBuf buffer) {
        int revision = buffer.readVarInt();
        int size = buffer.readVarInt();
        if (size < 0 || size > 256)
            throw new IllegalArgumentException("Invalid roosting recipe count: " + size);
        var recipes = new ArrayList<RoostingDisplayRecipe>(size);
        for (int i = 0; i < size; i++)
            recipes.add(RoostingDisplayRecipe.STREAM_CODEC.decode(buffer));
        return new RoostingDisplaySnapshot(revision, recipes);
    }
}
