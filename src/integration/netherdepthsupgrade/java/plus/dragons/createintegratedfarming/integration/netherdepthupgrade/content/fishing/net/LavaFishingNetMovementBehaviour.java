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

import com.scouter.netherdepthsupgrade.entity.LavaAnimal;
import com.scouter.netherdepthsupgrade.items.NDUItems;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetMovementBehaviour;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class LavaFishingNetMovementBehaviour extends AbstractFishingNetMovementBehaviour<LavaFishingNetContext> {
    @Override
    protected boolean canCaptureEntity(LivingEntity entity) {
        if (entity instanceof Enemy)
            return false;
        if (entity instanceof WaterAnimal || entity instanceof LavaAnimal) {
            var dimensions = entity.getDimensions(Pose.SWIMMING);
            float maxSize = CIFConfig.server().fishingNetCapturedCreatureMaxSize.getF();
            return dimensions.height() < maxSize && dimensions.width() < maxSize;
        }
        return false;
    }

    @Override
    protected LavaFishingNetContext getFishingNetContext(MovementContext context, ServerLevel level) {
        if (!(context.temporaryData instanceof LavaFishingNetContext)) {
            context.temporaryData = new LavaFishingNetContext(level, new ItemStack(NDUItems.LAVA_FISHING_ROD.asItem()));
        }
        return (LavaFishingNetContext) context.temporaryData;
    }
}
