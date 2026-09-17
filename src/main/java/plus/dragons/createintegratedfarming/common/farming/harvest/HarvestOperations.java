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

package plus.dragons.createintegratedfarming.common.farming.harvest;

import com.simibubi.create.AllTags.AllBlockTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;

/** Operations for validated multi-block crops; these do not infer a crop's structure or maturity. */
public final class HarvestOperations {
    private HarvestOperations() {}

    public static boolean canHarvest(AreaHarvestContext context, List<BlockPos> parts) {
        return parts.stream().allMatch(pos -> context.level().isLoaded(pos)
                && !AllBlockTags.NON_HARVESTABLE.matches(context.level().getBlockState(pos)));
    }

    public static boolean dropsEnabled(AreaHarvestContext context) {
        return context.level() instanceof ServerLevel
                && context.level().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
                && !context.level().restoringBlockSnapshots;
    }

    public static boolean harvestPlant(AreaHarvestContext context, List<BlockPos> parts,
            BlockPos anchor, BlockState replanted, Item seed) {
        if (!(context.level() instanceof ServerLevel level) || !canHarvest(context, parts)
                || !context.claimHarvest(anchor))
            return false;
        List<BlockState> states = parts.stream().map(level::getBlockState).toList();
        List<ItemStack> drops = new ArrayList<>();
        // Loot must see the complete original plant, before any neighbour can destroy another segment.
        for (int i = 0; i < parts.size(); i++) {
            BlockPos part = parts.get(i);
            BlockState state = states.get(i);
            if (dropsEnabled(context)) {
                drops.addAll(Block.getDrops(state, level, part, level.getBlockEntity(part), null, context.tool()));
                state.spawnAfterBreak(level, part, context.tool(), true);
            }
            level.levelEvent(2001, part, Block.getId(state));
        }
        for (int i = 0; i < parts.size(); i++)
            level.setBlock(parts.get(i), states.get(i).getFluidState().createLegacyBlock(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        if (context.replant() && replanted.canSurvive(level, anchor) && consumeSeed(context, drops, seed))
            level.setBlock(anchor, replanted, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        // Publish neighbour changes only once the whole plant is in its final state.
        for (int i = 0; i < parts.size(); i++) {
            BlockPos part = parts.get(i);
            level.updateNeighborsAt(part, states.get(i).getBlock());
            level.getBlockState(part).updateNeighbourShapes(level, part, Block.UPDATE_ALL);
        }
        drops.forEach(context::collect);
        return true;
    }

    private static boolean consumeSeed(AreaHarvestContext context, List<ItemStack> drops, Item seed) {
        for (ItemStack stack : drops) {
            if (!stack.isEmpty() && stack.is(seed)) {
                stack.shrink(1);
                return true;
            }
        }
        return !context.extractSeed(stack -> stack.is(seed), 1).isEmpty();
    }
}
