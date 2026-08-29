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

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CropBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.confluence.farming.harvest.ConfluenceCropHarvestBehaviour;

public final class ConfluenceHarvestBehaviours {
    private ConfluenceHarvestBehaviours() {}

    public static void register() {
        register(NatureBlocks.STELLAR_BLOSSOM.get(), FoodItems.STELLAR_BLOSSOM_SEED.get());
        register(NatureBlocks.CLOUDWEAVER.get(), FoodItems.CLOUDWEAVER_SEED.get());
        register(NatureBlocks.FLOATING_WHEAT.get(), FoodItems.FLOATING_WHEAT_SEED.get());
        register(ModBlocks.WATERLEAF.get(), FoodItems.WATERLEAF_SEED.get());
        register(ModBlocks.FIREBLOSSOM.get(), FoodItems.FIREBLOSSOM_SEED.get());
        register(ModBlocks.MOONGLOW.get(), FoodItems.MOONGLOW_SEED.get());
        register(ModBlocks.BLINKROOT.get(), FoodItems.BLINKROOT_SEED.get());
        register(ModBlocks.SHIVERTHORN.get(), FoodItems.SHIVERTHORN_SEED.get());
        register(ModBlocks.DAYBLOOM.get(), FoodItems.DAYBLOOM_SEED.get());
        register(ModBlocks.DEATHWEED.get(), FoodItems.DEATHWEED_SEED.get());
    }

    private static void register(CropBlock crop, Item seed) {
        CustomHarvestBehaviour.REGISTRY.register(crop, new ConfluenceCropHarvestBehaviour(crop, seed));
    }
}
