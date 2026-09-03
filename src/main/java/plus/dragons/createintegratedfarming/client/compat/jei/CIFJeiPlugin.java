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

package plus.dragons.createintegratedfarming.client.compat.jei;

import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.network.RoostingDisplayClientCache;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySnapshot;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

@JeiPlugin
public final class CIFJeiPlugin implements IModPlugin {
    public static final RecipeType<RoostingDisplayRecipe> ROOSTING = RecipeType.create(
            CIFCommon.ID, "roosting", RoostingDisplayRecipe.class);
    private static final ResourceLocation UID = CIFCommon.asResource("jei_plugin");

    private @Nullable IJeiRuntime runtime;
    private @Nullable Runnable removeCacheListener;
    private List<RoostingDisplayRecipe> activeRecipes = List.of();
    private int activeRevision = Integer.MIN_VALUE;

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RoostingCategory(
                registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RoostingDisplaySnapshot snapshot = RoostingDisplayClientCache.get();
        activeRecipes = snapshot.recipes();
        activeRevision = snapshot.revision();
        registration.addRecipes(ROOSTING, activeRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(CIFBlocks.ROOST.get(), ROOSTING);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        if (removeCacheListener != null)
            removeCacheListener.run();
        runtime = jeiRuntime;
        removeCacheListener = RoostingDisplayClientCache.addListener(this::onSnapshotUpdated);
        RoostingDisplaySnapshot snapshot = RoostingDisplayClientCache.get();
        if (snapshot.revision() != activeRevision)
            replaceRecipes(snapshot);
        else
            updateCategoryVisibility();
    }

    @Override
    public void onRuntimeUnavailable() {
        if (removeCacheListener != null) {
            removeCacheListener.run();
            removeCacheListener = null;
        }
        runtime = null;
        activeRecipes = List.of();
        activeRevision = Integer.MIN_VALUE;
    }

    private void onSnapshotUpdated(RoostingDisplaySnapshot snapshot) {
        if (runtime != null && snapshot.revision() != activeRevision)
            replaceRecipes(snapshot);
    }

    private void replaceRecipes(RoostingDisplaySnapshot snapshot) {
        if (runtime == null)
            return;
        var recipeManager = runtime.getRecipeManager();
        if (!activeRecipes.isEmpty())
            recipeManager.hideRecipes(ROOSTING, activeRecipes);
        activeRecipes = snapshot.recipes();
        activeRevision = snapshot.revision();
        if (!activeRecipes.isEmpty())
            recipeManager.addRecipes(ROOSTING, activeRecipes);
        updateCategoryVisibility();
    }

    private void updateCategoryVisibility() {
        if (runtime == null)
            return;
        if (activeRecipes.isEmpty())
            runtime.getRecipeManager().hideRecipeCategory(ROOSTING);
        else
            runtime.getRecipeManager().unhideRecipeCategory(ROOSTING);
    }
}
