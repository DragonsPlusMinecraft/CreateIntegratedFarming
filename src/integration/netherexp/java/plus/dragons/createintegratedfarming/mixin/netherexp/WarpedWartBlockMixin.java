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

package plus.dragons.createintegratedfarming.mixin.netherexp;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.jadenxgamer.netherexp.core.block.WarpedWartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Restriction(require = @Condition(Mods.NETHER_EXPANSION))
@Mixin(WarpedWartBlock.class)
public class WarpedWartBlockMixin {
    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void createintegratedfarming$keepImmatureHangingPlant(BlockState state, Direction direction,
            BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos,
            CallbackInfoReturnable<BlockState> cir) {
        // The lower half only grows at age 3. A replanted seed must survive updates before then.
        if (direction == Direction.DOWN && state.getValue(WarpedWartBlock.HALF) == DoubleBlockHalf.UPPER
                && state.getValue(WarpedWartBlock.AGE) < 3)
            cir.setReturnValue(state.canSurvive(level, pos) ? state : state.getFluidState().createLegacyBlock());
    }
}
