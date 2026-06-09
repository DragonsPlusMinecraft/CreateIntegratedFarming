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

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Restriction(require = @Condition(Mods.SABLE))
@Mixin(MovementBehaviour.class)
public interface MovementBehaviourMixin {
    @Inject(method = "collectOrDropItem", at = @At("HEAD"), cancellable = true)
    private void createintegratedfarming$sableDropWithoutContraption(MovementContext context, ItemStack stack, CallbackInfo ci) {
        if (context.contraption != null)
            return;
        ci.cancel();
        if (stack.isEmpty() || context.world == null)
            return;
        Vec3 position = context.position;
        if (position == null && context.localPos != null)
            position = context.localPos.getCenter();
        if (position == null)
            return;
        ItemEntity item = new ItemEntity(context.world, position.x, position.y, position.z, stack.copy());
        Vec3 motion = context.motion == null ? Vec3.ZERO : context.motion;
        item.setDeltaMovement(motion.add(0, 0.5F, 0).scale(context.world.random.nextFloat() * 0.3F));
        context.world.addFreshEntity(item);
    }
}
