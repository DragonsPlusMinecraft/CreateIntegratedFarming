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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createintegratedfarming.config.CIFConfig;

@Mixin(SeatBlock.class)
public class SeatBlockMixin {
    @ModifyExpressionValue(method = "updateEntityAfterFallOn", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/seat/SeatBlock;canBePickedUp(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean createintegratedfarming$ignoreLeashedEntity(boolean original, BlockGetter level, Entity entity) {
        if (entity instanceof Leashable leashable && leashable.isLeashed())
            return original && CIFConfig.server().leashedEntitySitsAutomatically.get();
        return original;
    }
}
