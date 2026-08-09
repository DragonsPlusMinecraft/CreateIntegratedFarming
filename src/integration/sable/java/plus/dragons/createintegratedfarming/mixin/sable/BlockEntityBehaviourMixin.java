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

package plus.dragons.createintegratedfarming.mixin.sable;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createdragonsplus.common.registry.CDPCapabilities;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Restriction(require = @Condition(Mods.SABLE))
@Mixin(BlockEntityBehaviour.class)
public class BlockEntityBehaviourMixin {
    @Inject(method = "get(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;", at = @At("HEAD"), cancellable = true, remap = false)
    private static <T extends BlockEntityBehaviour> void createintegratedfarming$queryBehaviourProviderForSable(
            BlockEntity blockEntity, BehaviourType<T> type, CallbackInfoReturnable<T> cir) {
        if (blockEntity == null || blockEntity instanceof SmartBlockEntity || !(blockEntity.getLevel() instanceof Level level))
            return;

        var provider = level.getCapability(
                CDPCapabilities.BEHAVIOUR_PROVIDER,
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity);
        if (provider == null)
            return;

        var behaviour = provider.getBehaviour(type);
        if (behaviour != null)
            cir.setReturnValue(behaviour);
    }
}
