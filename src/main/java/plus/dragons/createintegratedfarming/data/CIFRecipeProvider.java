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

import static com.simibubi.create.AllItems.*;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.*;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.*;
import static vectorwing.farmersdelight.common.registry.ModItems.*;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class CIFRecipeProvider extends RegistrateRecipeProvider {
    public CIFRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(CIFCommon.REGISTRATE, output, registries);
    }

    // Fallback recipe if Farmer's Delight is not loaded
    @Override
    protected void buildRecipes(RecipeOutput output) {
        shaped().output(ROOST)
                .define('#', CANVAS.get())
                .define('b', BAMBOO)
                .define('c', WHEAT)
                .pattern("b b")
                .pattern("#c#")
                .pattern("b#b")
                .unlockedBy("has_canvas", has(CANVAS.get()))
                .withCondition(ModIntegration.FARMERSDELIGHT.invertedCondition())
                .accept(output);
        shaped().output(FISHING_NET, 2)
                .define('#', SAFETY_NET.get())
                .define('/', Tags.Items.RODS_WOODEN)
                .define('a', ANDESITE_ALLOY)
                .pattern("#/")
                .pattern("/a")
                .unlockedBy("has_safety_net", has(SAFETY_NET.get()))
                .unlockedBy("has_andesite_alloy", has(ANDESITE_ALLOY))
                .withCondition(ModIntegration.FARMERSDELIGHT.invertedCondition())
                .accept(output);
    }
}
