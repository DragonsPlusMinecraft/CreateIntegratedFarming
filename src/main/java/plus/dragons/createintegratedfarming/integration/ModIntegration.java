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

package plus.dragons.createintegratedfarming.integration;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public enum ModIntegration {
    FARMERS_DELIGHT(Mods.FARMERS_DELIGHT),
    MY_NETHERS_DELIGHT(Mods.MY_NETHERS_DELIGHT),
    MMLIB(Mods.MMLIB),
    CREATE_ENCHANTABLE_MACHINERY(Mods.CREATE_ENCHANTABLE_MACHINERY),
    CREATE_CRAFT_AND_ADDITIONS(Mods.CREATE_CRAFT_AND_ADDITIONS),
    NETHER_DEPTHS_UPGRADE(Mods.NETHER_DEPTHS_UPGRADE),
    CRABBERS_DELIGHT(Mods.CRABBERS_DELIGHT),
    UNTITLED_DUCK(Mods.UNTITLED_DUCK),
    ENVIRONMENTAL(Mods.ENVIRONMENTAL),
    AUTUMNITY(Mods.AUTUMNITY),
    CULTURAL_DELIGHTS(Mods.CULTURAL_DELIGHTS),
    DELIGHT_O_FLIGHT(Mods.DELIGHT_O_FLIGHT),
    SABLE(Mods.SABLE),
    TWILIGHT_FOREST(Mods.TWILIGHT_FOREST),
    TWILIGHT_DELIGHT(Mods.TWILIGHT_DELIGHT);

    private final String id;

    ModIntegration(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean enabled() {
        return ModList.get().isLoaded(id);
    }

    public ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public ModLoadedCondition condition() {
        return new ModLoadedCondition(id);
    }

    public NotCondition invertedCondition() {
        return new NotCondition(new ModLoadedCondition(id));
    }

    public static class Mods {
        public static final String FARMERS_DELIGHT = "farmersdelight";
        public static final String MY_NETHERS_DELIGHT = "mynethersdelight";
        public static final String MMLIB = "mysterious_mountain_lib";
        public static final String CREATE_ENCHANTABLE_MACHINERY = "createenchantablemachinery";
        public static final String CREATE_CRAFT_AND_ADDITIONS = "createadditions";
        public static final String NETHER_DEPTHS_UPGRADE = "netherdepthsupgrade";
        public static final String CRABBERS_DELIGHT = "crabbersdelight";
        public static final String UNTITLED_DUCK = "untitledduckmod";
        public static final String ENVIRONMENTAL = "environmental";
        public static final String AUTUMNITY = "autumnity";
        public static final String CULTURAL_DELIGHTS = "culturaldelights";
        public static final String DELIGHT_O_FLIGHT = "delighto_flight";
        public static final String SABLE = "sable";
        public static final String TWILIGHT_FOREST = "twilightforest";
        public static final String TWILIGHT_DELIGHT = "twilightdelight";
    }
}
