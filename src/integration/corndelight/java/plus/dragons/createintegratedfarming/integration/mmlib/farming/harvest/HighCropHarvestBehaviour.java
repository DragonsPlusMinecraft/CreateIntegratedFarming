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

package plus.dragons.createintegratedfarming.integration.mmlib.farming.harvest;

import cn.mcmod_mmf.mmlib.block.HighCropBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class HighCropHarvestBehaviour implements CustomHarvestBehaviour {
    private final HighCropBlock crop;

    protected HighCropHarvestBehaviour(HighCropBlock crop) {
        this.crop = crop;
    }

    public static @Nullable HighCropHarvestBehaviour create(Block block) {
        if (block instanceof HighCropBlock crop)
            return new HighCropHarvestBehaviour(crop);
        return null;
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        Level level = context.world;
        boolean replant = CustomHarvestBehaviour.replant();
        int age = crop.getAge(state);
        if (age < crop.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        if (replant) {
            int growUpperAge = crop.getGrowUpperAge();
            if (age < growUpperAge)
                return;
            MutableBoolean seedSubtracted = new MutableBoolean(false);
            CustomHarvestBehaviour.harvestBlock(
                    level, pos,
                    crop.getStateForAge(growUpperAge).setValue(HighCropBlock.UPPER, state.getValue(HighCropBlock.UPPER)),
                    null,
                    CustomHarvestBehaviour.getHarvestTool(context),
                    1,
                    stack -> {
                        if (!seedSubtracted.getValue() && stack.is(crop.asItem())) {
                            stack.shrink(1);
                            seedSubtracted.setTrue();
                        }
                        behaviour.dropItem(context, stack);
                    });
        } else {
            destroy(level, behaviour, context, pos, state);
        }
    }

    protected void destroy(Level level, HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        boolean upper = state.getValue(HighCropBlock.UPPER);
        if (upper) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.is(crop))
                destroy(level, behaviour, context, abovePos, aboveState);
        }
        BlockHelper.destroyBlockAs(
                level,
                pos,
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1,
                stack -> behaviour.dropItem(context, stack));
    }
}
