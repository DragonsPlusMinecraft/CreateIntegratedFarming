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

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public abstract class AbstractFishingNetContext<T extends FishingHook> {
    protected final FishingNetFakePlayer player;
    protected final ItemStack fishingRod;
    protected final T fishingHook;
    protected final Set<BlockPos> visitedBlocks = new HashSet<>(
            Math.min(16, CIFConfig.server().fishingNetMaxRecordedBlocks.get()));
    public int timeUntilCatch;

    public AbstractFishingNetContext(ServerLevel level, ItemStack fishingRod) {
        this.player = new FishingNetFakePlayer(level);
        this.fishingRod = fishingRod;
        this.fishingHook = createFishingHook(level);
        this.fishingHook.setOwner(player);
        this.reset(level);
    }

    protected abstract T createFishingHook(ServerLevel level);

    @ApiStatus.Internal
    public abstract boolean isPosValidForFishing(ServerLevel level, BlockPos pos);

    public abstract LootTable getLootTable(ServerLevel level, BlockPos pos);

    public FishingNetFakePlayer getPlayer() {
        return player;
    }

    public T getFishingHook() {
        return fishingHook;
    }

    public ItemStack getFishingRod() {
        return fishingRod;
    }

    public void reset(ServerLevel level) {
        this.visitedBlocks.clear();
        this.timeUntilCatch = getCatchDelay(level);
    }

    @ApiStatus.Internal
    public int getCatchDelay(ServerLevel level) {
        int lureSpeed = (int) (EnchantmentHelper.getFishingTimeReduction(level, fishingRod, fishingHook) * 20.0F);
        return (Mth.nextInt(fishingHook.getRandom(), 100, 600) - lureSpeed) *
                CIFConfig.server().fishingNetCooldownMultiplier.get();
    }

    public boolean visitNewPositon(ServerLevel level, BlockPos pos) {
        if (isPosValidForFishing(level, pos)) {
            if (visitedBlocks.size() < CIFConfig.server().fishingNetMaxRecordedBlocks.get())
                visitedBlocks.add(pos);
            return true;
        }
        return false;
    }

    public LootParams buildCaptureLootContext(MovementContext context, ServerLevel level, LivingEntity entity) {
        player.setPos(context.position);
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ORIGIN, context.position)
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, fishingHook)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .create(LootContextParamSets.ENTITY);
    }

    public LootParams buildFishingLootContext(MovementContext context, ServerLevel level, BlockPos pos) {
        fishingHook.setPos(context.position);
        player.setPos(context.position);
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, context.position)
                .withParameter(LootContextParams.TOOL, fishingRod)
                .withParameter(LootContextParams.THIS_ENTITY, fishingHook)
                .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                .withLuck(EnchantmentHelper.getFishingLuckBonus(level, fishingRod, player))
                .create(LootContextParamSets.FISHING);
    }

    public boolean canCatch() {
        int maxRecorded = CIFConfig.server().fishingNetMaxRecordedBlocks.get();
        if (maxRecorded == 0)
            return true;
        return fishingHook.getRandom().nextInt(maxRecorded) < visitedBlocks.size();
    }

    public void invalidate(ServerLevel level) {
        fishingHook.discard();
        player.discard();
    }
}
