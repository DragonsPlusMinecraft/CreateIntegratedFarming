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

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.duck.DuckRoostBlockEntity;
import plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.goose.GooseRoostBlockEntity;

public class UntitledDuckBlockEntities {
    public static final BlockEntityEntry<DuckRoostBlockEntity> DUCK_ROOST = REGISTRATE
            .blockEntity("duck_roost", DuckRoostBlockEntity::new)
            .validBlock(UntitledDuckBlocks.DUCK_ROOST_NORMAL)
            .validBlock(UntitledDuckBlocks.DUCK_ROOST_CAMPBELL)
            .validBlock(UntitledDuckBlocks.DUCK_ROOST_FEMALE)
            .validBlock(UntitledDuckBlocks.DUCK_ROOST_PEKIN)
            .register();

    public static final BlockEntityEntry<GooseRoostBlockEntity> GOOSE_ROOST = REGISTRATE
            .blockEntity("goose_roost", GooseRoostBlockEntity::new)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_NORMAL)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_CANADIAN)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_GREYLAG)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_PING)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_SUS)
            .validBlock(UntitledDuckBlocks.GOOSE_ROOST_UNTITLED)
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(UntitledDuckBlockEntities.class);
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                ItemHandler.BLOCK,
                DUCK_ROOST.get(),
                DuckRoostBlockEntity::getItemHandler);
        event.registerBlockEntity(
                ItemHandler.BLOCK,
                GOOSE_ROOST.get(),
                GooseRoostBlockEntity::getItemHandler);
    }
}
