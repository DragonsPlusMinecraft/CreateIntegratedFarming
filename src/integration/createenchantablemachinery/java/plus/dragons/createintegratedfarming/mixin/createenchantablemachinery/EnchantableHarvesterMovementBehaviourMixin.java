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

package plus.dragons.createintegratedfarming.mixin.createenchantablemachinery;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterMovementBehaviour;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Restriction(require = @Condition(Mods.CREATE_ENCHANTABLE_MACHINERY))
@Mixin(EnchantableHarvesterMovementBehaviour.class)
public class EnchantableHarvesterMovementBehaviourMixin extends HarvesterMovementBehaviour {
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lio/github/cotrin8672/cem/content/block/harvester/EnchantableHarvesterMovementBehaviour;isValidCrop(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void createintegratedfarming$applyCustomHarvesterBehaviour(MovementContext context, BlockPos pos, CallbackInfo ci, @Local BlockState stateVisited) {
        var behaviour = CustomHarvestBehaviour.REGISTRY.get(stateVisited);
        if (behaviour == null)
            return;
        behaviour.harvest(this, context, pos, stateVisited);
        ci.cancel();
    }
}
