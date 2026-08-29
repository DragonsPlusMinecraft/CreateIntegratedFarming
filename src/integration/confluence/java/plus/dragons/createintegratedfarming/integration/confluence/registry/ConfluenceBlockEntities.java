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

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createintegratedfarming.integration.confluence.ranching.roost.ConfluenceDuckRoostBlockEntity;

public final class ConfluenceBlockEntities {
    public static final BlockEntityEntry<ConfluenceDuckRoostBlockEntity> DUCK_ROOST = REGISTRATE
            .blockEntity("confluence_duck_roost", ConfluenceDuckRoostBlockEntity::new)
            .validBlock(ConfluenceBlocks.DUCK_ROOST_MALLARD)
            .validBlock(ConfluenceBlocks.DUCK_ROOST_COMMON)
            .register();

    private ConfluenceBlockEntities() {}

    public static void register(IEventBus modBus) {
        modBus.register(ConfluenceBlockEntities.class);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                ItemHandler.BLOCK,
                DUCK_ROOST.get(),
                ConfluenceDuckRoostBlockEntity::getItemHandler);
    }
}
