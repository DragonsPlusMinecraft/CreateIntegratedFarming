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

package plus.dragons.createintegratedfarming.integration.confluence;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.mixed.IMinecraftServer;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchContext;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProvider;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;

public class ConfluenceFishingNetCatchProvider implements FishingNetCatchProvider {
    @Override
    public List<ItemStack> getCatch(FishingNetCatchContext context) {
        var tables = context.level().getServer().reloadableRegistries();
        var fishingTable = context.medium() == FishingNetMedium.LAVA
                ? ModLootTables.FISHING_LAVA
                : ModLootTables.FISHING;
        List<ItemStack> result = tables.getLootTable(fishingTable).getRandomItems(context.lootParams());
        if (context.random().nextInt(10) != 0)
            return result;

        var crateTable = IMinecraftServer.isHardmode(context.level().getServer())
                ? ModLootTables.CRATE_HARDMODE
                : ModLootTables.CRATE;
        LootParams crateParams = new LootParams.Builder(context.level())
                .withParameter(LootContextParams.ORIGIN, context.origin())
                .withParameter(LootContextParams.THIS_ENTITY, context.fishingHook())
                .create(LootContextParamSets.GIFT);
        return tables.getLootTable(crateTable).getRandomItems(crateParams);
    }
}
