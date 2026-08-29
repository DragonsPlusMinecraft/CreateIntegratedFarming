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

package plus.dragons.createintegratedfarming.common.fishing.net;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.FishingHook.OpenWaterType;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class FishingNetContext extends AbstractFishingNetContext<FishingHook> {
    public FishingNetContext(ServerLevel level, ItemStack fishingRod) {
        super(level, fishingRod);
    }

    @Override
    protected FishingHook createFishingHook(ServerLevel level) {
        return new FishingHook(EntityType.FISHING_BOBBER, level);
    }

    @Override
    public boolean isPosValidForFishing(ServerLevel level, BlockPos pos) {
        return fishingHook.getOpenWaterTypeForBlock(pos) == OpenWaterType.INSIDE_WATER;
    }

    @Override
    protected FishingNetMedium getMedium() {
        return FishingNetMedium.WATER;
    }

    @Override
    protected boolean isOpenFluid(ServerLevel level, BlockPos pos) {
        if (!CIFConfig.server().fishingNetChecksOpenWater.get())
            return false;
        OpenWaterType previous = OpenWaterType.INVALID;
        for (int y = -1; y <= 2; y++) {
            OpenWaterType current = fishingHook.getOpenWaterTypeForArea(
                    pos.offset(-2, y, -2), pos.offset(2, y, 2));
            if (current == OpenWaterType.INVALID
                    || current == OpenWaterType.ABOVE_WATER && previous == OpenWaterType.INVALID
                    || current == OpenWaterType.INSIDE_WATER && previous == OpenWaterType.ABOVE_WATER)
                return false;
            previous = current;
        }
        return true;
    }
}
