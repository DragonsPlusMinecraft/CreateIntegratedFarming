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

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodFluid;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodItem;

public class CIFDataMaps {
    private static final TagKey<Fluid> PLANT_OIL = TagKey.create(
            Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "plantoil"));

    public static final DataMapType<Item, ChickenFoodItem> CHICKEN_FOOD_ITEMS = DataMapType
            .builder(CIFCommon.asResource("chicken_food"), Registries.ITEM, ChickenFoodItem.CODEC)
            .synced(ChickenFoodItem.CODEC, true)
            .build();
    public static final DataMapType<Fluid, ChickenFoodFluid> CHICKEN_FOOD_FLUIDS = DataMapType
            .builder(CIFCommon.asResource("chicken_food"), Registries.FLUID, ChickenFoodFluid.CODEC)
            .synced(ChickenFoodFluid.CODEC, true)
            .build();

    public static void register(IEventBus modBus) {
        modBus.register(CIFDataMaps.class);
        REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, CIFDataMaps::generate);
    }

    @SubscribeEvent
    public static void registerDataMaps(final RegisterDataMapTypesEvent event) {
        event.register(CHICKEN_FOOD_ITEMS);
        event.register(CHICKEN_FOOD_FLUIDS);
    }

    public static void generate(RegistrateDataMapProvider provider) {
        provider.builder(CHICKEN_FOOD_ITEMS)
                .add(ItemTags.CHICKEN_FOOD, new ChickenFoodItem(
                        ConstantInt.of(2400),
                        UniformInt.of(400, 800),
                        Optional.empty()), false);
        provider.builder(CHICKEN_FOOD_FLUIDS)
                .add(PLANT_OIL, new ChickenFoodFluid(
                        ConstantInt.of(2400),
                        UniformInt.of(400, 800),
                        100), false);
    }
}
