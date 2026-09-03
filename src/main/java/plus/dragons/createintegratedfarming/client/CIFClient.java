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

package plus.dragons.createintegratedfarming.client;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.network.RoostingDisplayClientCache;

@Mod(value = CIFCommon.ID, dist = Dist.CLIENT)
public class CIFClient {
    public CIFClient(IEventBus modBus) {
        modBus.addListener(EventPriority.LOWEST, CIFClient::init);
        NeoForge.EVENT_BUS.addListener(CIFClient::onLogout);
    }

    public static void init(final FMLClientSetupEvent event) {
        CIFPartialModels.init();
        PonderIndex.addPlugin(new CIFPonderPlugin());
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        RoostingDisplayClientCache.clear();
    }
}
