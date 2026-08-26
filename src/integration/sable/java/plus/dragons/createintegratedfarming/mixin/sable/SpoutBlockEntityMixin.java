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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;
import plus.dragons.createintegratedfarming.integration.sable.SableSpouting;

@Restriction(require = @Condition(Mods.SABLE))
@Mixin(SpoutBlockEntity.class)
public abstract class SpoutBlockEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/behaviour/spouting/BlockSpoutingBehaviour;get(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/api/behaviour/spouting/BlockSpoutingBehaviour;"))
    private BlockSpoutingBehaviour createintegratedfarming$sableFindSpoutingTarget(
            Level level, BlockPos pos, Operation<BlockSpoutingBehaviour> original) {
        BlockSpoutingBehaviour direct = original.call(level, pos);
        if (direct != null || level.isClientSide)
            return direct;
        return SableSpouting.PROXY;
    }
}
