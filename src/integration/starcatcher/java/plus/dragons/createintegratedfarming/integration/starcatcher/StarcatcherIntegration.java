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

package plus.dragons.createintegratedfarming.integration.starcatcher;

import com.wdiscute.starcatcher.data.attachments.FishingGuideAttachment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetFakePlayer;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

@Mod(CIFCommon.ID)
public class StarcatcherIntegration {
    public StarcatcherIntegration(IEventBus modBus) {
        if (ModIntegration.STARCATCHER.enabled()) {
            modBus.register(new Common());
            NeoForge.EVENT_BUS.register(new FishingEvents());
            if (FMLLoader.getDist() == Dist.CLIENT)
                modBus.register(new Client());
        }
    }

    public static class Common {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            var provider = new StarcatcherFishingNetCatchProvider();
            FishingNetCatchProviders.register(
                    ModIntegration.STARCATCHER.asResource("fishing_net"), FishingNetMedium.WATER, provider);
            FishingNetCatchProviders.register(
                    ModIntegration.STARCATCHER.asResource("fishing_net"), FishingNetMedium.LAVA, provider);
        }
    }

    public static class Client {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            StarcatcherFishingNetPonderExample.register();
        }
    }

    public static class FishingEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void itemFished(ItemFishedEvent event) {
            if (event.getHookEntity().getPlayerOwner() instanceof FishingNetFakePlayer player)
                FishingGuideAttachment.setFishedRod(player, true);
        }
    }
}
