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

package plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest;

import alabaster.hearthandharvest.common.block.CornStalkBlock;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class CornHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    // Starting with 1.4, picking corn rolls the block loot table, including corn husks.
    private static final boolean USE_LOOT_TABLE = ModList.get().getModContainerById(ModIntegration.HEARTH_AND_HARVEST.id())
            .orElseThrow().getModInfo().getVersion().compareTo(new DefaultArtifactVersion("1.4.0")) >= 0;

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(context.level() instanceof ServerLevel level))
            return false;
        BlockState current = level.getBlockState(pos);
        if (!current.is(HHModBlocks.CORN_STALK.get()) || current.getValue(CornStalkBlock.AGE) < 4)
            return false;
        if (HarvestOperations.dropsEnabled(context)) {
            if (USE_LOOT_TABLE)
                Block.getDrops(current, level, pos, null, null, context.tool()).forEach(context::collect);
            else
                context.collect(new ItemStack(HHModItems.CORN.get(), current.getValue(CornStalkBlock.AGE) == 5 ? 2 : 1));
        }
        level.setBlock(pos, current.setValue(CornStalkBlock.AGE, 3), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
