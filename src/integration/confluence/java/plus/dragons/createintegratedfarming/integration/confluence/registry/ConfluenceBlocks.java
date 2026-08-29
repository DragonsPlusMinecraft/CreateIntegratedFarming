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

package plus.dragons.createintegratedfarming.integration.confluence.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.ROOST;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.confluence.terraentity.entity.animal.Duck;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.integration.confluence.ranching.roost.ConfluenceDuckRoostBlock;

public final class ConfluenceBlocks {
    public static final BlockEntry<ConfluenceDuckRoostBlock> DUCK_ROOST_MALLARD = registerDuckRoost("confluence_duck_roost_mallard", "Mallard Duck Roost", Duck.MALLARD_ID);
    public static final BlockEntry<ConfluenceDuckRoostBlock> DUCK_ROOST_COMMON = registerDuckRoost("confluence_duck_roost_common", "Common Duck Roost", Duck.COMMON_ID);

    private ConfluenceBlocks() {}

    private static BlockEntry<ConfluenceDuckRoostBlock> registerDuckRoost(
            String path, String name, int variant) {
        return REGISTRATE.block(path, properties -> new ConfluenceDuckRoostBlock(properties, ROOST, variant))
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
            event.accept(DUCK_ROOST_MALLARD);
            event.accept(DUCK_ROOST_COMMON);
        }
    }

    public static void register(IEventBus modBus) {
        modBus.register(ConfluenceBlocks.class);
    }
}
