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

package plus.dragons.createintegratedfarming.integration.neapolitan;

import com.teamabnormals.neapolitan.common.block.MintBlock;
import com.teamabnormals.neapolitan.core.registry.NeapolitanBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest.AdzukiHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest.BananaHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest.MintHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest.VanillaHarvestBehaviour;

@Mod(CIFCommon.ID)
public class NeapolitanIntegration {
    public NeapolitanIntegration(IEventBus modBus) {
        if (ModIntegration.NEAPOLITAN.enabled())
            modBus.register(new Common());
    }

    public static class Common {
        @SubscribeEvent
        public void commonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                var vanilla = new VanillaHarvestBehaviour();
                CustomHarvestBehaviour.REGISTRY.register(NeapolitanBlocks.VANILLA_VINE.get(), vanilla);
                CustomHarvestBehaviour.REGISTRY.register(NeapolitanBlocks.VANILLA_VINE_PLANT.get(), vanilla);
                CustomHarvestBehaviour.REGISTRY.register(NeapolitanBlocks.BANANA_BUNDLE.get(), new BananaHarvestBehaviour());
                CustomHarvestBehaviour.REGISTRY.register(NeapolitanBlocks.ADZUKI_SPROUTS.get(), new AdzukiHarvestBehaviour());
            });
            CustomHarvestBehaviour.REGISTRY.registerProvider(block -> block instanceof MintBlock ? new MintHarvestBehaviour() : null);
        }
    }
}
