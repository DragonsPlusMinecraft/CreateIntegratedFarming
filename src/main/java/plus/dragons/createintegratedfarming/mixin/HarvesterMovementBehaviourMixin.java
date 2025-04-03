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

package plus.dragons.createintegratedfarming.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.api.harvest.CustomHarvestBehaviour;

@Mixin(HarvesterMovementBehaviour.class)
public class HarvesterMovementBehaviourMixin {
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/harvester/HarvesterMovementBehaviour;isValidCrop(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), cancellable = true)
    private void createintegratedfarming$applyCustomHarvesterBehaviour(MovementContext context, BlockPos pos, CallbackInfo ci, @Local BlockState stateVisited) {
        var behaviour = CustomHarvestBehaviour.REGISTRY.get(stateVisited);
        if (behaviour == null)
            return;
        behaviour.harvest((HarvesterMovementBehaviour) (Object) this, context, pos, stateVisited);
        ci.cancel();
    }

    // This should effectly fix rope logged tomatoes and other crops that has extra properties
    // Consider PR this to Create
    @Redirect(method = "cutCrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CropBlock;getStateForAge(I)Lnet/minecraft/world/level/block/state/BlockState;"), require = 0)
    private BlockState createintegratedfarming$fixCropState(CropBlock crop, int age, @Local(argsOnly = true) BlockState state) {
        return state.setValue(((CropBlockAccessor) crop).invokeGetAgeProperty(), age);
    }
}
