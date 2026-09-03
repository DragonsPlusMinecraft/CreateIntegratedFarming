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

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySnapshot;

public record RoostingDisplayPayload(RoostingDisplaySnapshot snapshot) implements CustomPacketPayload {
    public static final Type<RoostingDisplayPayload> TYPE = new Type<>(CIFCommon.asResource("roosting_display_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RoostingDisplayPayload> STREAM_CODEC = StreamCodec.ofMember(
            RoostingDisplayPayload::encode,
            RoostingDisplayPayload::decode);

    private void encode(RegistryFriendlyByteBuf buffer) {
        RoostingDisplaySnapshot.STREAM_CODEC.encode(buffer, snapshot);
    }

    private static RoostingDisplayPayload decode(RegistryFriendlyByteBuf buffer) {
        return new RoostingDisplayPayload(RoostingDisplaySnapshot.STREAM_CODEC.decode(buffer));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
