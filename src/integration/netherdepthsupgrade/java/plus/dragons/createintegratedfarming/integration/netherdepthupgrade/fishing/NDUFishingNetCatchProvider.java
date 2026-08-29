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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.fishing;

import com.scouter.netherdepthsupgrade.loot.NDULootTables;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchContext;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProvider;

public class NDUFishingNetCatchProvider implements FishingNetCatchProvider {
    @Override
    public List<ItemStack> getCatch(FishingNetCatchContext context) {
        var lootTable = context.level().dimension() == Level.NETHER
                ? NDULootTables.NETHER_FISHING
                : NDULootTables.LAVA_FISHING;
        return context.level()
                .getServer()
                .reloadableRegistries()
                .getLootTable(lootTable)
                .getRandomItems(context.lootParams());
    }
}
