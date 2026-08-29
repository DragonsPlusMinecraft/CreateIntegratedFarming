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

import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public abstract class AbstractFishingNetMovementBehaviour<T extends AbstractFishingNetContext<?>> implements MovementBehaviour {
    @ApiStatus.Internal
    public abstract T createFishingNetContext(ServerLevel level);

    protected abstract T getFishingNetContext(MovementContext context, ServerLevel level);

    @ApiStatus.Internal
    public boolean canCaptureEntity(LivingEntity entity) {
        return FishingNetEntityCaptures.canCapture(entity);
    }

    protected void onCaptureEntity(MovementContext context, ServerLevel level, T fishing, LivingEntity entity) {
        if (!entity.isBaby() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            var lootTable = level.getServer().reloadableRegistries().getLootTable(entity.getLootTable());
            var lootParams = fishing.buildCaptureLootContext(context, level, entity);
            lootTable.getRandomItems(lootParams, entity.getLootTableSeed(), item -> collectOrDropItem(context, item));
            if (CIFConfig.server().fishingNetCapturedCreatureDropExpNugget.get()) {
                int experience = EventHooks.getExperienceDrop(entity, fishing.player, entity.getExperienceReward(level, entity));
                collectOrDropItem(context, new ItemStack(AllItems.EXP_NUGGET.get(), Math.ceilDiv(experience, 3)));
            }
        }
        entity.discard();
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world instanceof ServerLevel level) {
            var fishing = getFishingNetContext(context, level);
            if (fishing.timeUntilCatch > 0)
                fishing.timeUntilCatch--;
            if (level.getGameTime() % 20 == 0 && CIFConfig.server().fishingNetCaptureCreatureInWater.get()) {
                AABB area = context.state.getShape(level, context.localPos).bounds()
                        .expandTowards(context.motion.scale(5))
                        .move(context.position)
                        .inflate(0.2);
                level.getEntities(EntityTypeTest.forClass(LivingEntity.class), area, this::canCaptureEntity)
                        .forEach(entity -> this.onCaptureEntity(context, level, fishing, entity));
            }
        }
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world instanceof ServerLevel level) {
            var fishing = getFishingNetContext(context, level);
            var isValid = fishing.visitNewPositon(level, pos);
            if (!isValid || fishing.timeUntilCatch > 0)
                return;
            if (fishing.canCatch()) {
                var catchContext = fishing.buildFishingCatchContext(context, level, pos);
                var loots = new ArrayList<>(FishingNetCatchProviders.getCatch(catchContext));
                var event = NeoForge.EVENT_BUS.post(new ItemFishedEvent(loots, 0, fishing.getFishingHook()));
                if (!event.isCanceled()) {
                    loots.forEach(stack -> collectOrDropItem(context, stack));
                }
            }
            fishing.reset(level);
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.world instanceof ServerLevel level
                && context.temporaryData instanceof AbstractFishingNetContext<?> fishing) {
            fishing.invalidate(level);
            context.temporaryData = null;
        }
    }
}
