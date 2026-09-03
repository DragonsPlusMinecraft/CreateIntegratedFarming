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

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.network.RoostingDisplayPayload;

@ApiStatus.Internal
public final class RoostingDisplaySync {
    private static MinecraftServer cachedServer;
    private static RoostingDisplaySnapshot cachedSnapshot;
    private static int revision;

    private RoostingDisplaySync() {}

    public static void register() {
        NeoForge.EVENT_BUS.register(RoostingDisplaySync.class);
    }

    @SubscribeEvent
    public static synchronized void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        boolean fullReload = event.getPlayer() == null;
        if (server != cachedServer) {
            cachedServer = server;
            revision = 0;
            cachedSnapshot = null;
        }
        if (cachedSnapshot == null || fullReload)
            cachedSnapshot = RoostingDisplaySnapshotBuilder.build(server, revision++);
        var payload = new RoostingDisplayPayload(cachedSnapshot);
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    @SubscribeEvent
    public static synchronized void onServerStopped(ServerStoppedEvent event) {
        if (event.getServer() == cachedServer) {
            cachedServer = null;
            cachedSnapshot = null;
            revision = 0;
        }
    }
}
