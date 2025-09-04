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

package plus.dragons.createintegratedfarming.mixin.netherdepthupgrade;

import com.llamalad7.mixinextras.sugar.Local;
import com.scouter.netherdepthsupgrade.events.ForgeEvents;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetFakePlayer;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Restriction(require = @Condition(Mods.NETHER_DEPTHS_UPGRADE))
@Mixin(ForgeEvents.class)
public class ForgeEventsMixin {
    @Inject(method = "changeFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private static void createintegratedfarming$noFishingEntitiesForFishingNet(ItemFishedEvent event, CallbackInfo ci, @Nullable @Local Player player) {
        if (player == null || player instanceof FishingNetFakePlayer)
            ci.cancel();
    }
}
