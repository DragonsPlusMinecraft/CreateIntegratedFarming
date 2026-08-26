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
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterHarvesting.HarvestResult;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterMovementBehaviour;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;
import plus.dragons.createintegratedfarming.integration.sable.SableVacuumHarvesting;

@Restriction(require = @Condition(Mods.SABLE))
@Mixin(VacuumHarvesterMovementBehaviour.class)
public abstract class VacuumHarvesterMovementBehaviourMixin {
    @WrapOperation(method = "harvestArea", at = @At(value = "INVOKE", target = "Lplus/dragons/createintegratedfarming/common/farming/vacuum/VacuumHarvesterHarvesting;harvestArea(Lplus/dragons/createintegratedfarming/api/harvester/AreaHarvestContext;Lnet/minecraft/core/BlockPos;I)Lplus/dragons/createintegratedfarming/common/farming/vacuum/VacuumHarvesterHarvesting$HarvestResult;"))
    private HarvestResult createintegratedfarming$sableHarvestAcrossLevels(
            AreaHarvestContext harvestContext,
            BlockPos center,
            int range,
            Operation<HarvestResult> original,
            MovementContext context) {
        SubLevelAccess source = context.contraption == null || context.contraption.entity == null
                ? null
                : Sable.HELPER.getContaining(context.contraption.entity);
        return SableVacuumHarvesting.harvestArea(
                harvestContext,
                center,
                range,
                source,
                () -> original.call(harvestContext, center, range));
    }
}
