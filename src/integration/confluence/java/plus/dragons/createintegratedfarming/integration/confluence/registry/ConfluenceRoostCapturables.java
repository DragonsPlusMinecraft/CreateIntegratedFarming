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

import static org.confluence.mod.common.init.entity.CritterEntities.DUCK;

import org.confluence.mod.common.entity.animal.Duck;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturableProvider;

public final class ConfluenceRoostCapturables {
    private ConfluenceRoostCapturables() {}

    public static void register() {
        RoostCapturableProvider.REGISTRY.register(DUCK.get(), entity -> {
            if (entity instanceof Duck duck && duck.getVariant() == Duck.Variant.COMMON)
                return ConfluenceBlocks.DUCK_ROOST_COMMON.get();
            return ConfluenceBlocks.DUCK_ROOST_MALLARD.get();
        });
    }
}
