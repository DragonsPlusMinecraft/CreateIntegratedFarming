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

package plus.dragons.createintegratedfarming.common.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createintegratedfarming.common.ranching.chicken.ChickenCoopBlockEntity;

public class CIFBlockEntities {
    public static final BlockEntityEntry<ChickenCoopBlockEntity> CHICKEN_COOP = REGISTRATE
            .blockEntity("chicken_coop", ChickenCoopBlockEntity::new)
            .validBlock(CIFBlocks.CHICKEN_COOP)
            .onRegister(type -> BlockSpoutingBehaviour.BY_BLOCK_ENTITY
                    .register(type, ChickenCoopBlockEntity::fillBySpout))
            .register();

    public static void register(IEventBus modBus) {
        modBus.register(CIFBlockEntities.class);
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ItemHandler.BLOCK, CHICKEN_COOP.get(), ChickenCoopBlockEntity::getItemHandler);
    }
}
