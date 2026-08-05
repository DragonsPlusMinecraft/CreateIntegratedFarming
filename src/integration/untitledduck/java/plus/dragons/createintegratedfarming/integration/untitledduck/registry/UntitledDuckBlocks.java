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

package plus.dragons.createintegratedfarming.integration.untitledduck.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.ROOST;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.duck.DuckRoostBlock;
import plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.goose.GooseRoostBlock;

public class UntitledDuckBlocks {
    public static final BlockEntry<DuckRoostBlock> DUCK_ROOST_NORMAL = registerDuckRoost("duck_roost_normal", (byte) 0);
    public static final BlockEntry<DuckRoostBlock> DUCK_ROOST_FEMALE = registerDuckRoost("duck_roost_female", (byte) 1);
    public static final BlockEntry<DuckRoostBlock> DUCK_ROOST_CAMPBELL = registerDuckRoost("duck_roost_campbell", (byte) 2);
    public static final BlockEntry<DuckRoostBlock> DUCK_ROOST_PEKIN = registerDuckRoost("duck_roost_pekin", (byte) 3);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_NORMAL = registerGooseRoost("goose_roost_normal", (byte) 0);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_CANADIAN = registerGooseRoost("goose_roost_canadian", (byte) 1);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_GREYLAG = registerGooseRoost("goose_roost_greylag", (byte) 2);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_PING = registerGooseRoost("goose_roost_ping", (byte) 3);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_SUS = registerGooseRoost("goose_roost_sus", (byte) 4);
    public static final BlockEntry<GooseRoostBlock> GOOSE_ROOST_UNTITLED = registerGooseRoost("goose_roost_untitled", (byte) 5);

    private static BlockEntry<DuckRoostBlock> registerDuckRoost(String path, byte variant) {
        return REGISTRATE.block(path, prop -> new DuckRoostBlock(prop, ROOST, variant))
                .lang("Duck Roost")
                .properties(prop -> {
                    var result = prop.strength(1.5F).sound(SoundType.BAMBOO_WOOD);
                    // Registrate's loot provider cannot add a mod-loaded condition, so these tables are maintained manually.
                    return DatagenModLoader.isRunningDataGen() ? result.noLootTable() : result;
                })
                .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
                .item()
                .build()
                .register();
    }

    private static BlockEntry<GooseRoostBlock> registerGooseRoost(String path, byte variant) {
        return REGISTRATE.block(path, prop -> new GooseRoostBlock(prop, ROOST, variant))
                .lang("Goose Roost")
                .properties(prop -> {
                    var result = prop.strength(1.5F).sound(SoundType.BAMBOO_WOOD);
                    // Registrate's loot provider cannot add a mod-loaded condition, so these tables are maintained manually.
                    return DatagenModLoader.isRunningDataGen() ? result.noLootTable() : result;
                })
                .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
                .item()
                .build()
                .register();
    }

    @SubscribeEvent
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CIFCreativeModeTabs.BASE.getKey()) {
            event.accept(DUCK_ROOST_NORMAL);
            event.accept(DUCK_ROOST_FEMALE);
            event.accept(DUCK_ROOST_CAMPBELL);
            event.accept(DUCK_ROOST_PEKIN);
            event.accept(GOOSE_ROOST_NORMAL);
            event.accept(GOOSE_ROOST_CANADIAN);
            event.accept(GOOSE_ROOST_GREYLAG);
            event.accept(GOOSE_ROOST_PING);
            event.accept(GOOSE_ROOST_SUS);
            event.accept(GOOSE_ROOST_UNTITLED);
        }
    }

    public static void register(IEventBus modBus) {
        modBus.register(UntitledDuckBlocks.class);
    }
}
