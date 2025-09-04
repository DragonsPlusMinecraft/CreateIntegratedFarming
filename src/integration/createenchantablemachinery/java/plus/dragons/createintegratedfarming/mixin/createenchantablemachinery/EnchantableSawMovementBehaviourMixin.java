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

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawMovementBehaviour;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.api.saw.SawableBlockTags;

@Mixin(EnchantableSawMovementBehaviour.class)
public class EnchantableSawMovementBehaviourMixin extends SawMovementBehaviour {
    @Final
    @Shadow
    private Map<MovementContext, ItemStack> enchantedTools;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V"))
    private void createintegratedfarming$handleFragileVerticalPlants(MovementContext context, BlockPos pos, CallbackInfo ci) {
        var level = context.world;
        var state = level.getBlockState(pos);
        if (state.is(SawableBlockTags.FRAGILE_VERTICAL_PLANTS)) {
            TreeCutter.findTree(context.world, pos, state).destroyBlocks(level, enchantedTools.get(context), null,
                    (dropPos, stack) -> this.dropItemFromCutTree(context, dropPos, stack));
        }
    }
}
