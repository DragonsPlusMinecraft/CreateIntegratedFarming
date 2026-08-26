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
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetMovementBehaviour;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;
import plus.dragons.createintegratedfarming.integration.sable.SableFishingNetController;
import plus.dragons.createintegratedfarming.integration.sable.SableFishingNetControllerAccess;

@Restriction(require = @Condition(Mods.SABLE))
@Mixin(ServerLevelPlot.class)
public abstract class ServerLevelPlotMixin implements SableFishingNetControllerAccess {
    @Unique
    private @Nullable SableFishingNetController createintegratedfarming$fishingNets;

    @Inject(method = "onBlockChange", at = @At("TAIL"))
    private void createintegratedfarming$trackFishingNet(
            BlockPos pos, BlockState state, CallbackInfo ci) {
        MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get(state);
        AbstractFishingNetMovementBehaviour<?> fishingBehaviour = movementBehaviour instanceof AbstractFishingNetMovementBehaviour<?> fishing ? fishing : null;
        if (createintegratedfarming$fishingNets == null && fishingBehaviour == null)
            return;
        if (createintegratedfarming$fishingNets == null)
            createintegratedfarming$fishingNets = new SableFishingNetController((ServerLevelPlot) (Object) this);
        createintegratedfarming$fishingNets.onBlockChange(pos, fishingBehaviour, state);
        if (createintegratedfarming$fishingNets.isEmpty()) {
            createintegratedfarming$fishingNets.close();
            createintegratedfarming$fishingNets = null;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void createintegratedfarming$tickFishingNets(CallbackInfo ci) {
        if (createintegratedfarming$fishingNets != null)
            createintegratedfarming$fishingNets.tick();
    }

    @Override
    public void createintegratedfarming$closeFishingNetController() {
        if (createintegratedfarming$fishingNets == null)
            return;
        createintegratedfarming$fishingNets.close();
        createintegratedfarming$fishingNets = null;
    }
}
