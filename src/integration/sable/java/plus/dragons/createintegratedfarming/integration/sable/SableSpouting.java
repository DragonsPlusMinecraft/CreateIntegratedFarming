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

package plus.dragons.createintegratedfarming.integration.sable;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockSpoutingBehaviours;

public final class SableSpouting {
    public static final BlockSpoutingBehaviour PROXY = SableSpouting::fillAcrossLevels;

    private SableSpouting() {}

    private static int fillAcrossLevels(
            Level level, BlockPos sourcePos, SpoutBlockEntity spout, FluidStack fluid, boolean simulate) {
        if (!(level instanceof ServerLevel serverLevel))
            return 0;
        SubLevelAccess source = Sable.HELPER.getContaining(spout);
        Integer result = Sable.HELPER.runIncludingSubLevels(
                serverLevel,
                sourcePos.getCenter(),
                !simulate,
                source,
                (targetSubLevel, targetPos) -> fillManaged(serverLevel, targetPos, spout, fluid, simulate));
        return result == null ? 0 : result;
    }

    private static Integer fillManaged(
            ServerLevel level, BlockPos pos, SpoutBlockEntity spout, FluidStack fluid, boolean simulate) {
        BlockSpoutingBehaviour behaviour = BlockSpoutingBehaviour.get(level, pos);
        if (!CIFBlockSpoutingBehaviours.isManaged(behaviour))
            return null;
        int amount = behaviour.fillBlock(level, pos, spout, fluid, simulate);
        return amount > 0 ? amount : null;
    }
}
