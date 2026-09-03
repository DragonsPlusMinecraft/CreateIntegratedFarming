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

package plus.dragons.createintegratedfarming.integration.untitledduck;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLLoader;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.untitledduck.ponder.UntitledDuckPonderPlugin;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlockEntities;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlocks;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckCapturables;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckRoostingDisplayProfiles;

@Mod(CIFCommon.ID)
public class UntitledDuckIntegration {
    public UntitledDuckIntegration(IEventBus modBus) {
        if (ModIntegration.UNTITLED_DUCK.enabled()) {
            modBus.register(new Common(modBus));
            if (FMLLoader.getDist() == Dist.CLIENT)
                modBus.register(new Client());
        }
    }

    public static class Common {
        public Common(IEventBus modBus) {
            this.modBus = modBus;
        }

        IEventBus modBus;

        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            UntitledDuckBlocks.register(modBus);
            UntitledDuckBlockEntities.register(modBus);
        }

        @SubscribeEvent
        public void commonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(UntitledDuckCapturables::register);
            event.enqueueWork(UntitledDuckRoostingDisplayProfiles::register);
        }
    }

    public static class Client {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            UntitledDuckPonderPlugin.register();
        }
    }
}
