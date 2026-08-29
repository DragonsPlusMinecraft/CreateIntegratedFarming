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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturableProvider;
import plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost.VanillaBackportChickenRoostBlock;

public final class VanillaBackportRoostCapturables {
    private VanillaBackportRoostCapturables() {}

    public static void register() {
        RoostCapturableProvider.REGISTRY.register(EntityType.CHICKEN, entity -> {
            if (!(entity instanceof Chicken chicken))
                return null;
            CompoundTag tag = new CompoundTag();
            chicken.saveWithoutId(tag);
            return switch (tag.getString(VanillaBackportChickenRoostBlock.VARIANT_TAG)) {
                case "minecraft:warm" -> VanillaBackportBlocks.CHICKEN_ROOST_WARM.get();
                case "minecraft:cold" -> VanillaBackportBlocks.CHICKEN_ROOST_COLD.get();
                default -> null;
            };
        });
    }
}
