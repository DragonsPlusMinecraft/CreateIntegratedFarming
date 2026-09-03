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

package plus.dragons.createintegratedfarming.common.ranching.roost.display;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodFluid;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodItem;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.FluidFeedDisplay;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.IntRange;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.ItemFeedDisplay;
import plus.dragons.createintegratedfarming.common.registry.CIFDataMaps;

@ApiStatus.Internal
public final class RoostingDisplaySnapshotBuilder {
    private RoostingDisplaySnapshotBuilder() {}

    public static RoostingDisplaySnapshot build(MinecraftServer server, int revision) {
        var lootParser = new RoostingLootTableParser(server);
        Map<ResourceKey<LootTable>, RoostingLootTableParser.Result> lootCache = new LinkedHashMap<>();
        List<FluidFeedDisplay> fluidFeeds = resolveFluidFeeds();
        var recipes = RoostingDisplayProfiles.all().stream()
                .map(profile -> buildRecipe(profile, lootParser, lootCache, fluidFeeds))
                .sorted(Comparator.comparing(recipe -> recipe.id().toString()))
                .toList();
        return new RoostingDisplaySnapshot(revision, recipes);
    }

    private static RoostingDisplayRecipe buildRecipe(
            RoostingDisplayProfile profile,
            RoostingLootTableParser lootParser,
            Map<ResourceKey<LootTable>, RoostingLootTableParser.Result> lootCache,
            List<FluidFeedDisplay> fluidFeeds) {
        ResourceLocation representative = BuiltInRegistries.BLOCK.getKey(profile.representativeRoost().get());
        List<ResourceLocation> equivalents = profile.equivalentRoosts().stream()
                .map(supplier -> BuiltInRegistries.BLOCK.getKey(supplier.get()))
                .filter(id -> !id.equals(representative))
                .distinct()
                .sorted()
                .toList();
        var loot = lootCache.computeIfAbsent(profile.productionLootTable(), lootParser::parse);
        return new RoostingDisplayRecipe(
                profile.id(),
                representative,
                equivalents,
                new IntRange(profile.minimumProductionTicks(), profile.maximumProductionTicks()),
                resolveItemFeeds(profile.itemFeedSource()),
                fluidFeeds,
                loot.outputs(),
                loot.status());
    }

    private static List<ItemFeedDisplay> resolveItemFeeds(ItemFeedSource source) {
        var feeds = new ArrayList<ItemFeedDisplay>();
        if (source instanceof ItemFeedSource.ChickenDataMap) {
            BuiltInRegistries.ITEM.holders().forEach(holder -> {
                ChickenFoodItem food = holder.getData(CIFDataMaps.CHICKEN_FOOD_ITEMS);
                if (food != null) {
                    feeds.add(new ItemFeedDisplay(
                            new ItemStack(holder.value()),
                            range(food.progress()),
                            range(food.cooldown()),
                            food.usingConvertsTo().orElse(ItemStack.EMPTY)));
                }
            });
        } else if (source instanceof ItemFeedSource.ItemTag tagged) {
            for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tagged.tag())) {
                ItemStack ingredient = new ItemStack(holder.value());
                feeds.add(new ItemFeedDisplay(
                        ingredient,
                        tagged.progress(),
                        tagged.cooldown(),
                        ingredient.getCraftingRemainingItem()));
            }
        }
        return feeds.stream()
                .collect(java.util.stream.Collectors.toMap(
                        feed -> feed.ingredient().getItem(),
                        feed -> feed,
                        (left, right) -> left,
                        IdentityHashMap::new))
                .values()
                .stream()
                .sorted(Comparator.comparing(feed -> BuiltInRegistries.ITEM
                        .getKey(feed.ingredient().getItem())
                        .toString()))
                .toList();
    }

    private static List<FluidFeedDisplay> resolveFluidFeeds() {
        Map<Fluid, FluidFeedDisplay> feeds = new IdentityHashMap<>();
        BuiltInRegistries.FLUID.holders().forEach(holder -> {
            ChickenFoodFluid food = holder.getData(CIFDataMaps.CHICKEN_FOOD_FLUIDS);
            if (food == null)
                return;
            Fluid fluid = holder.value();
            Fluid source = fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
            if (source.defaultFluidState().isEmpty())
                return;
            feeds.putIfAbsent(
                    source,
                    new FluidFeedDisplay(
                            new FluidStack(source, food.amount()), range(food.progress()), range(food.cooldown())));
        });
        return feeds.values().stream()
                .sorted(Comparator.comparing(feed -> BuiltInRegistries.FLUID
                        .getKey(feed.ingredient().getFluid())
                        .toString()))
                .toList();
    }

    private static IntRange range(IntProvider provider) {
        return new IntRange(provider.getMinValue(), provider.getMaxValue());
    }
}
