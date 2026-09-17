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

package plus.dragons.createintegratedfarming.integration.mynethersdelight.registry;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.soytutta.mynethersdelight.common.block.LetiosCompostBlock;
import com.soytutta.mynethersdelight.common.registry.MNDBlocks;
import com.soytutta.mynethersdelight.common.tag.MNDTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class MNDBlockSpoutingBehaviors {
    public static void register() {
        CIFBlockSpoutingBehaviours.registerManaged(
                BuiltInRegistries.BLOCK.get(ModIntegration.MY_NETHERS_DELIGHT.asResource("letios_compost")),
                MNDBlockSpoutingBehaviors::fillLetiosCompost);
    }

    private static int fillLetiosCompost(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack fluid, boolean simulate) {
        if (level.isClientSide || fluid.getAmount() < 250 || !fluid.is(MNDTags.LETEOS_BOOSTER)
                || !level.dimensionType().ultraWarm())
            return 0;
        BlockState state = level.getBlockState(pos);
        if (!state.is(MNDBlocks.LETIOS_COMPOST.get()))
            return 0;
        if (!simulate) {
            int stage = state.getValue(LetiosCompostBlock.FORGOTING);
            level.setBlockAndUpdate(pos, stage == 9 ? MNDBlocks.RESURGENT_SOIL.get().defaultBlockState()
                    : state.setValue(LetiosCompostBlock.FORGOTING, stage + 1));
        }
        return 250;
    }
}
