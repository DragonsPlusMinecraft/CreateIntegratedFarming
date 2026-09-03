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

package plus.dragons.createintegratedfarming.common;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.dragons.createdragonsplus.common.CDPRegistrate;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.common.network.CIFPackets;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySync;
import plus.dragons.createintegratedfarming.common.registry.CIFArmInteractionPoints;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockEntities;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.common.registry.CIFDataMaps;
import plus.dragons.createintegratedfarming.common.registry.CIFRoostCapturables;
import plus.dragons.createintegratedfarming.common.registry.CIFRoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.config.CIFConfig;

@Mod(CIFCommon.ID)
public class CIFCommon {
    public static final String ID = "create_integrated_farming";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create: Integrated Farming");
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public CIFCommon(IEventBus modBus, ModContainer modContainer) {
        REGISTRATE.addRawLang("create_integrated_farming.goggles.roost.next_output", "Next output: %s");
        REGISTRATE.addRawLang("create_integrated_farming.goggles.roost.ready", "Ready");
        REGISTRATE.addRawLang(
                "create_integrated_farming.goggles.roost.output_inventory_full", "Output inventory full");
        REGISTRATE.addRawLang("create_integrated_farming.jei.roosting", "Roosting");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.optional_feeding", "Optional feeding");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.natural_time", "Produces in %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.applies_to", "Also applies to:");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.natural_cycle", "Natural production cycle: %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.feeding_optional",
                "Feeding is optional; production also proceeds naturally.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.feeding_effect",
                "Feeding only shortens the time remaining until the next output.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.returns", "Returns: %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.spout", "Feed using a Spout");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.consumes", "Consumes %s mB per feeding");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.progress", "Advances next output by %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.cooldown", "Feeding cooldown: %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.output_count", "Output count: %s");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.conditional_output",
                "Actual output depends on server loot-table conditions.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.complex_output",
                "This output cannot be fully determined from the server loot table.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.missing_output",
                "No production loot table is loaded.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.no_static_output",
                "No statically verifiable output.");
        REGISTRATE.addRawLang(
                "create_integrated_farming.jei.roosting.loot_modifier_note",
                "Global loot modifiers and dynamic Java drops may change the actual output.");
        REGISTRATE.registerEventListeners(modBus);
        CIFCreativeModeTabs.register(modBus);
        CIFBlocks.register(modBus);
        CIFBlockEntities.register(modBus);
        CIFArmInteractionPoints.register(modBus);
        CIFDataMaps.register(modBus);
        CIFPackets.register(modBus);
        RoostingDisplaySync.register();
        FishingNetCatchProviders.register(asResource("vanilla"), FishingNetMedium.WATER, context -> context.level()
                .getServer()
                .reloadableRegistries()
                .getLootTable(BuiltInLootTables.FISHING)
                .getRandomItems(context.lootParams()));
        modBus.register(this);
        modBus.register(new CIFConfig(modContainer));
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CIFBlockSpoutingBehaviours::register);
        event.enqueueWork(CIFRoostCapturables::register);
        event.enqueueWork(CIFRoostingDisplayProfiles::register);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
