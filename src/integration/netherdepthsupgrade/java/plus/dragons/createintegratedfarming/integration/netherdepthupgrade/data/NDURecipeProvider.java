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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.data;

import static com.simibubi.create.AllItems.ANDESITE_ALLOY;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shaped;
import static plus.dragons.createintegratedfarming.integration.netherdepthupgrade.registry.NDUBlocks.LAVA_FISHING_NET;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class NDURecipeProvider extends RegistrateRecipeProvider {
    public NDURecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(CIFCommon.REGISTRATE, output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
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
                .withCondition(ModIntegration.NETHER_DEPTHS_UPGRADE.condition())
                .accept(output);
    }
}
