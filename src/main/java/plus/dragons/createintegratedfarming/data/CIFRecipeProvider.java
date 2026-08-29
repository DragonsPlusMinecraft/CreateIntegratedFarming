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

package plus.dragons.createintegratedfarming.data;

import static com.simibubi.create.AllBlocks.*;
import static com.simibubi.create.AllItems.*;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.*;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.*;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.OrCondition;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class CIFRecipeProvider extends RegistrateRecipeProvider {
    public CIFRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(CIFCommon.REGISTRATE, output, registries);
    }

    // Fallback recipe if Farmer's Delight is not loaded
    @Override
    protected void buildRecipes(RecipeOutput output) {
        shapeless().output(VACUUM_HARVESTER)
                .require(MECHANICAL_HARVESTER)
                .require(ENCASED_FAN)
                .require(BRASS_CASING)
                .require(CHUTE)
                .unlockedBy("has_brass_casing", has(BRASS_CASING))
                .accept(output);
        shaped().output(ROOST)
                .withId(CIFCommon.asResource("fallback_roost"))
                .define('#', HAY_BLOCK)
                .define('b', BAMBOO)
                .define('c', WHEAT)
                .pattern("b b")
                .pattern("#c#")
                .pattern("b#b")
                .unlockedBy("has_hay_block", has(HAY_BLOCK))
                .withCondition(ModIntegration.FARMERS_DELIGHT.invertedCondition())
                .accept(output);
        shaped().output(FISHING_NET, 2)
                .withId(CIFCommon.asResource("fallback_fishing_net"))
                .define('#', BROWN_WOOL)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('a', ANDESITE_ALLOY)
                .pattern("#/")
                .pattern("/a")
                .unlockedBy("has_brown_wool", has(BROWN_WOOL))
                .unlockedBy("has_andesite_alloy", has(ANDESITE_ALLOY))
                .withCondition(ModIntegration.FARMERS_DELIGHT.invertedCondition())
                .accept(output);
        shaped().output(LAVA_FISHING_NET, 2)
                .define('#', CHAIN)
                .define('/', BLAZE_ROD)
                .define('a', ANDESITE_ALLOY)
                .pattern("###")
                .pattern("##/")
                .pattern("#/a")
                .unlockedBy("has_chain", has(CHAIN))
                .unlockedBy("has_blaze_rod", has(BLAZE_ROD))
                .unlockedBy("has_andesite_alloy", has(ANDESITE_ALLOY))
                .withCondition(new OrCondition(List.of(
                        ModIntegration.NETHER_DEPTHS_UPGRADE.condition(),
                        ModIntegration.TIDE.condition(),
                        ModIntegration.STARCATCHER.condition(),
                        ModIntegration.CONFLUENCE.condition())))
                .accept(output);
    }

    @Override
    public String getName() {
        return "Create: Integrated Farming Fallback Recipes";
    }
}
