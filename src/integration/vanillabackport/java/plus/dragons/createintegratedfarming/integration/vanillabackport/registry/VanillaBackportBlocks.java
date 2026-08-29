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

package plus.dragons.createintegratedfarming.integration.vanillabackport.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.ROOST;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost.VanillaBackportChickenRoostBlock;

public final class VanillaBackportBlocks {
    public static final BlockEntry<VanillaBackportChickenRoostBlock> CHICKEN_ROOST_WARM = registerChickenRoost(
            "vanillabackport_chicken_roost_warm",
            "Warm Chicken Roost",
            VanillaBackportChickenRoostBlock.WARM_VARIANT);
    public static final BlockEntry<VanillaBackportChickenRoostBlock> CHICKEN_ROOST_COLD = registerChickenRoost(
            "vanillabackport_chicken_roost_cold",
            "Cold Chicken Roost",
            VanillaBackportChickenRoostBlock.COLD_VARIANT);

    private VanillaBackportBlocks() {}

    private static BlockEntry<VanillaBackportChickenRoostBlock> registerChickenRoost(
            String path, String name, ResourceLocation variant) {
        return REGISTRATE.block(
                path,
                properties -> new VanillaBackportChickenRoostBlock(properties, ROOST, variant))
                .lang(name)
                .properties(properties -> {
                    var result = properties.strength(1.5F).sound(SoundType.BAMBOO_WOOD);
                    return DatagenModLoader.isRunningDataGen() ? result.noLootTable() : result;
                })
                .blockstate((context, provider) -> provider.horizontalBlock(context.get(), AssetLookup.standardModel(context, provider)))
                .item()
                .build()
                .register();
    }

    @SubscribeEvent
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CIFCreativeModeTabs.BASE.getKey()) {
            event.accept(CHICKEN_ROOST_WARM);
            event.accept(CHICKEN_ROOST_COLD);
        }
    }

    public static void register(IEventBus modBus) {
        modBus.register(VanillaBackportBlocks.class);
    }
}
