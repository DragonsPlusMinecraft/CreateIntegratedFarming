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

package plus.dragons.createintegratedfarming.common.farming.vacuum;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.StandardAreaHarvests;

public final class VacuumHarvesterHarvesting {
    public static final int MAX_PARTICLE_SOURCES = 12;

    private VacuumHarvesterHarvesting() {}

    public static HarvestResult harvestArea(AreaHarvestContext context, BlockPos center, int range) {
        int harvested = 0;
        List<BlockPos> particleSources = new ArrayList<>(MAX_PARTICLE_SOURCES);
        for (int y = center.getY() - 1; y <= center.getY() + 1; y++) {
            for (int x = center.getX() - range; x <= center.getX() + range; x++) {
                for (int z = center.getZ() - range; z <= center.getZ() + range; z++) {
                    BlockPos target = new BlockPos(x, y, z);
                    if (!context.level().isLoaded(target)
                            || !StandardAreaHarvests.harvest(context, target, context.level().getBlockState(target)))
                        continue;
                    harvested++;
                    if (particleSources.size() < MAX_PARTICLE_SOURCES) {
                        particleSources.add(target);
                        continue;
                    }
                    int replacement = context.level().random.nextInt(harvested);
                    if (replacement < MAX_PARTICLE_SOURCES)
                        particleSources.set(replacement, target);
                }
            }
        }
        return new HarvestResult(harvested, List.copyOf(particleSources));
    }

    public record HarvestResult(int harvested, List<BlockPos> particleSources) {}
}
