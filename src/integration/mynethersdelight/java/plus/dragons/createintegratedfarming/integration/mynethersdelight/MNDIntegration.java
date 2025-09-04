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

package plus.dragons.createintegratedfarming.integration.mynethersdelight;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLLoader;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.ponder.MNDPonderPlugin;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDBlockSpoutingBehaviors;

@Mod(CIFCommon.ID)
public class MNDIntegration {
    public MNDIntegration(IEventBus modBus) {
        if (ModIntegration.MYNETHERSDELIGHT.enabled()) {
            modBus.register(new Common());
            if (FMLLoader.getDist() == Dist.CLIENT)
                modBus.register(new Client());
        }
    }

    public static class Common {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            MNDArmInteractionPointTypes.register();
        }

        @SubscribeEvent
        public void commonSetup(final FMLCommonSetupEvent event) {
            MNDBlockSpoutingBehaviors.register();
        }
    }

    public static class Client {
        @SubscribeEvent
        public void construct(final FMLConstructModEvent event) {
            MNDPonderPlugin.register();
        }
    }
}
