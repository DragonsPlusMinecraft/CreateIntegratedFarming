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

package plus.dragons.createintegratedfarming.integration.farmersdelight.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createdragonsplus.common.registry.CDPCapabilities;
import plus.dragons.createintegratedfarming.integration.farmersdelight.logistics.basket.BasketBehaviourProvider;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

public class FDBlockEntities {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                CDPCapabilities.BEHAVIOUR_PROVIDER,
                ModBlockEntityTypes.BASKET.get(),
                (be, ignored) -> new BasketBehaviourProvider(be));
    }
}
