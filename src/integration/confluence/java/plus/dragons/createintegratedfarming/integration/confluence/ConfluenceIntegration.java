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

package plus.dragons.createintegratedfarming.integration.confluence;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ModelEvent;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.confluence.client.model.ConfluenceDuckModelLoader;
import plus.dragons.createintegratedfarming.integration.confluence.registry.ConfluenceBlockEntities;
import plus.dragons.createintegratedfarming.integration.confluence.registry.ConfluenceBlocks;
import plus.dragons.createintegratedfarming.integration.confluence.registry.ConfluenceHarvestBehaviours;
import plus.dragons.createintegratedfarming.integration.confluence.registry.ConfluenceRoostCapturables;

@Mod(CIFCommon.ID)
public class ConfluenceIntegration {
    public ConfluenceIntegration(IEventBus modBus) {
        if (ModIntegration.CONFLUENCE.enabled()) {
            modBus.register(new Common(modBus));
            if (FMLLoader.getDist() == Dist.CLIENT)
                modBus.register(new Client());
        }
    }

    public static class Common {
        private final IEventBus modBus;

        public Common(IEventBus modBus) {
            this.modBus = modBus;
        }

        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            ConfluenceBlocks.register(modBus);
            ConfluenceBlockEntities.register(modBus);
            var provider = new ConfluenceFishingNetCatchProvider();
            FishingNetCatchProviders.register(
                    ModIntegration.CONFLUENCE.asResource("fishing_net"), FishingNetMedium.WATER, provider);
            FishingNetCatchProviders.register(
                    ModIntegration.CONFLUENCE.asResource("fishing_net"), FishingNetMedium.LAVA, provider);
        }

        @SubscribeEvent
        public void commonSetup(final FMLCommonSetupEvent event) {
            ConfluenceHarvestBehaviours.register();
            event.enqueueWork(ConfluenceRoostCapturables::register);
        }
    }

    public static class Client {
        @SubscribeEvent
        public void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
            event.register(CIFCommon.asResource("confluence_duck"), ConfluenceDuckModelLoader.INSTANCE);
        }

        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            ConfluenceFishingNetPonderExample.register();
        }
    }
}
