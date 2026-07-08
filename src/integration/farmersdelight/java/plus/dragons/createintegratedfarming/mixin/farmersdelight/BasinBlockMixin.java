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

package plus.dragons.createintegratedfarming.mixin.farmersdelight;

import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import vectorwing.farmersdelight.common.block.BasketBlock;
import vectorwing.farmersdelight.common.block.entity.BasketBlockEntity;

@Restriction(require = @Condition(ModIntegration.Mods.FARMERS_DELIGHT))
@Mixin(BasinBlock.class)
public class BasinBlockMixin {
    @Inject(method = "canOutputTo", at = @At("RETURN"), cancellable = true)
    private static void createintegratedfarming$canOutputToBasket(BlockGetter world, BlockPos basinPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ())
            return;

        BlockPos neighbour = basinPos.relative(direction);
        BlockState neighbourState = world.getBlockState(neighbour);
        if (FunnelBlock.isFunnel(neighbourState)) {
            if (FunnelBlock.getFunnelFacing(neighbourState) == direction)
                return;
        } else if (!neighbourState.getCollisionShape(world, neighbour).isEmpty()) {
            return;
        }

        BlockPos output = neighbour.below();
        if (!(world.getBlockEntity(output) instanceof BasketBlockEntity basket))
            return;
        if (canInsertIntoBasket(basket.getBlockState(), direction))
            cir.setReturnValue(true);
    }

    private static boolean canInsertIntoBasket(BlockState state, Direction side) {
        Direction facing = state.getValue(BasketBlock.FACING);
        return switch (facing) {
            case UP -> true;
            case DOWN -> false;
            default -> facing == side.getOpposite();
        };
    }
}
