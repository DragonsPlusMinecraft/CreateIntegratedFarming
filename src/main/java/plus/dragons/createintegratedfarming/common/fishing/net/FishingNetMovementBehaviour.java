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
import java.util.List;

import com.simibubi.create.content.kinetics.saw.SawBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class FishingNetMovementBehaviour implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if (context.world instanceof ServerLevel level) {
            var fishing = getFishingNetContext(context, level);
            if (fishing.timeUntilCatch > 0)
                fishing.timeUntilCatch--;
            if (level.getGameTime() % 20 == 0 && CIFConfig.server().fishingNetCaptureSmallLivingBeingInWater.get()){
                AABB effectiveArea = context.state.getShape(level,context.localPos).bounds().expandTowards(context.motion.multiply(5,5,5)).move(context.position).inflate(0.2);
                var entities = level.getEntities((Entity) null, effectiveArea, entity -> {
                    if(entity instanceof WaterAnimal animal){ // QUESTION: Should we support all mobs?
                        return animal.getBbWidth() <= CIFConfig.server().fishingNetCapturedLivingBeingMaxSize.get() &&
                                animal.getBbHeight() <= CIFConfig.server().fishingNetCapturedLivingBeingMaxSize.get();
                    } return false;
                });
                entities.forEach(entity -> {
                    var animal = (WaterAnimal) entity;
                    if (!animal.isBaby() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                        var damageSource = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL),context.position);
                        var lootTable = level.getServer().reloadableRegistries().getLootTable(animal.getLootTable());
                        var lootParams = (new LootParams.Builder(level))
                                .withParameter(LootContextParams.THIS_ENTITY, animal)
                                .withParameter(LootContextParams.ORIGIN, animal.position())
                                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, context.contraption.entity)
                                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, context.contraption.entity)
                                .withLuck(EnchantmentHelper.getFishingLuckBonus(level, getFishingNetContext(context, level).fishingRod, context.contraption.entity));
                        lootTable.getRandomItems(lootParams.create(LootContextParamSets.ENTITY), animal.getLootTableSeed(), item -> dropItem(context,item));
                        if(CIFConfig.server().fishingNetCapturedLivingBeingDropExpNugget.get()){
                            int reward = EventHooks.getExperienceDrop(animal, null, animal.getExperienceReward(level, entity));
                            int count = reward / 3;
                            if (reward > 0 && count == 0) count = 1;
                            dropItem(context, new ItemStack(AllItems.EXP_NUGGET.get(), count));
                        }
                        animal.discard();
                    }
                });
            }
        }
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world instanceof ServerLevel level) {
            var fishing = getFishingNetContext(context, level);
            var inWater = fishing.visitNewPositon(level, pos);
            if (!inWater || fishing.timeUntilCatch > 0)
                return;
            if (fishing.canCatch()) {
                var params = fishing.buildLootContext(context, level, pos);
                LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
                List<ItemStack> loots = lootTable.getRandomItems(params);
                var event = NeoForge.EVENT_BUS.post(new ItemFishedEvent(loots, 0, fishing.getFishingHook()));
                if (!event.isCanceled()) {
                    loots.forEach(stack -> dropItem(context, stack));
                }
            }
            fishing.reset(level);
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.temporaryData instanceof FishingNetContext fishing && context.world instanceof ServerLevel level) {
            fishing.invalidate(level);
        }
    }

    protected FishingNetContext getFishingNetContext(MovementContext context, ServerLevel level) {
        if (!(context.temporaryData instanceof FishingNetContext)) {
            context.temporaryData = new FishingNetContext(level, new ItemStack(Items.FISHING_ROD));
        }
        return (FishingNetContext) context.temporaryData;
    }
}
