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

package plus.dragons.createintegratedfarming.integration.netherexp;

import net.jadenxgamer.netherexp.core.block.WarpedWartBlock;
import net.jadenxgamer.netherexp.registry.JNEBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.CerebrageHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.SorrowsquashHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.WarpedWartHarvestBehaviour;

@Mod(CIFCommon.ID)
public class NetherExpansionIntegration {
    public NetherExpansionIntegration(IEventBus modBus) {
        if (ModIntegration.NETHER_EXPANSION.enabled())
            modBus.register(new Common());
    }

    public static class Common {
        @SubscribeEvent
        public void commonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                CustomHarvestBehaviour.REGISTRY.register(JNEBlocks.CEREBRAGE_SKULL.get(), new CerebrageHarvestBehaviour());
                CustomHarvestBehaviour.REGISTRY.register(JNEBlocks.SORROWSQUASH.get(), new SorrowsquashHarvestBehaviour());
            });
            CustomHarvestBehaviour.REGISTRY.registerProvider(block -> block instanceof WarpedWartBlock ? new WarpedWartHarvestBehaviour() : null);
        }
    }
}
