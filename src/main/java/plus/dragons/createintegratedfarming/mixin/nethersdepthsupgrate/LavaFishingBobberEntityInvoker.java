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

package plus.dragons.createintegratedfarming.mixin.nethersdepthsupgrate;

import com.scouter.netherdepthsupgrade.entity.entities.LavaFishingBobberEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Constants;

@Restriction(require = @Condition(Constants.NETHER_DEPTHS_UPGRADE))
@Mixin(LavaFishingBobberEntity.class)
public interface LavaFishingBobberEntityInvoker {
    @Invoker(value = "calculateOpenLava")
    boolean invokeCalculateOpenLava(BlockPos pos);
}
