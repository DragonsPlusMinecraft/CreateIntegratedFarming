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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.content.fishing.net;

import com.scouter.netherdepthsupgrade.entity.NDUEntity;
import com.scouter.netherdepthsupgrade.entity.entities.LavaFishingBobberEntity;
import com.scouter.netherdepthsupgrade.loot.NDULootTables;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetContext;
import plus.dragons.createintegratedfarming.mixin.netherdepthupgrade.LavaFishingBobberEntityInvoker;

public class LavaFishingNetContext extends AbstractFishingNetContext<LavaFishingBobberEntity> {
    public LavaFishingNetContext(ServerLevel level, ItemStack fishingRod) {
        super(level, fishingRod);
    }

    @Override
    protected LavaFishingBobberEntity createFishingHook(ServerLevel level) {
        return new LavaFishingBobberEntity(NDUEntity.LAVA_BOBBER.get(), level);
    }

    @Override
    protected boolean isPosValidForFishing(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.LAVA) &&
                level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    @Override
    public LootTable getLootTable(ServerLevel level, BlockPos pos) {
        var registries = level.getServer().reloadableRegistries();
        if (level.dimension() == Level.NETHER)
            return registries.getLootTable(NDULootTables.NETHER_FISHING);
        return registries.getLootTable(NDULootTables.LAVA_FISHING);
    }

    public LootParams buildFishingLootContext(MovementContext context, ServerLevel level, BlockPos pos) {
        fishingHook.openWater = ((LavaFishingBobberEntityInvoker) fishingHook).invokeCalculateOpenLava(pos);
        return super.buildFishingLootContext(context, level, pos);
    }
}
