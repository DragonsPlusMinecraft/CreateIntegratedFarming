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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;
import plus.dragons.createintegratedfarming.integration.sable.SableFishingNetItemReceiver;

@Pseudo
@Restriction(require = {
        @Condition(Mods.SABLE),
        @Condition(Mods.SIMULATED),
})
@Mixin(targets = "dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlockEntity")
public abstract class AugerShaftBlockEntityMixin implements SableFishingNetItemReceiver {
    @Shadow
    public Direction flowDirection;

    @Shadow
    public abstract ItemStack onRecieveItem(ItemStack item, BlockPos fromPos);

    @Shadow
    public abstract boolean removed();

    @Shadow
    public abstract boolean isActive();

    @Override
    public boolean createintegratedfarming$acceptsFrom(BlockPos pos) {
        return flowDirection != null
                && ((BlockEntity) (Object) this).getBlockPos().relative(flowDirection.getOpposite()).equals(pos);
    }

    @Override
    public boolean createintegratedfarming$isUsable() {
        return !removed() && isActive();
    }

    @Override
    public ItemStack createintegratedfarming$receive(ItemStack stack, BlockPos fromPos) {
        return onRecieveItem(stack, fromPos);
    }
}
