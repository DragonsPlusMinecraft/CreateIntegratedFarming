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

package plus.dragons.createintegratedfarming.integration.confluence.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;

public class ConfluenceCropHarvestBehaviour implements CustomHarvestBehaviour {
    private final CropBlock crop;
    private final Item seed;

    public ConfluenceCropHarvestBehaviour(CropBlock crop, Item seed) {
        this.crop = crop;
        this.seed = seed;
    }

    @Override
    public void harvest(
            HarvesterMovementBehaviour behaviour,
            MovementContext context,
            BlockPos pos,
            BlockState state) {
        if (!isReady(state, CustomHarvestBehaviour.partial()))
            return;
        BlockState result = CustomHarvestBehaviour.replant()
                ? crop.getStateForAge(0)
                : Blocks.AIR.defaultBlockState();
        CustomHarvestBehaviour.harvestBlock(
                context.world,
                pos,
                result,
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1.0F,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!isReady(state, context.harvestPartiallyGrown()))
            return false;

        List<ItemStack> drops = new ArrayList<>();
        CustomHarvestBehaviour.harvestBlock(
                context.level(),
                pos,
                Blocks.AIR.defaultBlockState(),
                null,
                context.tool(),
                1.0F,
                drops::add);

        BlockState replanted = crop.getStateForAge(0);
        if (context.replant()
                && replanted.canSurvive(context.level(), pos)
                && consumeSeed(context, drops))
            context.level().setBlockAndUpdate(pos, replanted);
        drops.forEach(context::collect);
        return true;
    }

    private boolean isReady(BlockState state, boolean partial) {
        int age = crop.getAge(state);
        return partial ? age > 0 : age == crop.getMaxAge();
    }

    private boolean consumeSeed(AreaHarvestContext context, List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (!drop.is(seed))
                continue;
            drop.shrink(1);
            return true;
        }
        return !context.extractSeed(stack -> stack.is(seed), 1).isEmpty();
    }
}
