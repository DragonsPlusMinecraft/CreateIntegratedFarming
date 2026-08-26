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

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.StandardAreaHarvests;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterHarvesting;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterHarvesting.HarvestResult;

public final class SableVacuumHarvesting {
    private SableVacuumHarvesting() {}

    public static HarvestResult harvestArea(
            AreaHarvestContext context,
            BlockPos center,
            int range,
            @Nullable SubLevelAccess sourceSubLevel,
            Supplier<HarvestResult> sourceHarvest) {
        if (!(context.level() instanceof ServerLevel level))
            return sourceHarvest.get();

        AABB sourceBounds = new AABB(
                center.getX() - range,
                center.getY() - 1,
                center.getZ() - range,
                center.getX() + range + 1,
                center.getY() + 2,
                center.getZ() + range + 1);
        BoundingBox3d globalBounds = new BoundingBox3d(sourceBounds);
        if (sourceSubLevel != null)
            globalBounds.transform(sourceSubLevel.logicalPose());

        List<ServerSubLevel> targets = new ArrayList<>();
        for (SubLevel candidate : Sable.HELPER.getAllIntersecting(level, globalBounds)) {
            if (candidate != sourceSubLevel && candidate instanceof ServerSubLevel serverSubLevel)
                targets.add(serverSubLevel);
        }
        if (sourceSubLevel == null && targets.isEmpty())
            return sourceHarvest.get();

        HarvestResult sourceResult = sourceHarvest.get();
        ParticleReservoir result = new ParticleReservoir(level, sourceResult);
        if (sourceSubLevel != null)
            scanTarget(context, sourceBounds, globalBounds, sourceSubLevel, null, result);
        for (ServerSubLevel target : targets)
            scanTarget(context, sourceBounds, globalBounds, sourceSubLevel, target, result);
        return result.build();
    }

    private static void scanTarget(
            AreaHarvestContext context,
            AABB sourceBounds,
            BoundingBox3d globalBounds,
            @Nullable SubLevelAccess sourceSubLevel,
            @Nullable ServerSubLevel targetSubLevel,
            ParticleReservoir result) {
        BoundingBox3d targetBounds = new BoundingBox3d(globalBounds);
        BoundingBox3ic plotBounds = null;
        if (targetSubLevel != null) {
            targetBounds.transformInverse(targetSubLevel.logicalPose());
            plotBounds = targetSubLevel.getPlot().getBoundingBox();
        }

        int minX = Mth.floor(targetBounds.minX());
        int minY = Mth.floor(targetBounds.minY());
        int minZ = Mth.floor(targetBounds.minZ());
        int maxX = Mth.floor(targetBounds.maxX());
        int maxY = Mth.floor(targetBounds.maxY());
        int maxZ = Mth.floor(targetBounds.maxZ());
        if (plotBounds != null) {
            minX = Math.max(minX, plotBounds.minX());
            minY = Math.max(minY, plotBounds.minY());
            minZ = Math.max(minZ, plotBounds.minZ());
            maxX = Math.min(maxX, plotBounds.maxX());
            maxY = Math.min(maxY, plotBounds.maxY());
            maxZ = Math.min(maxZ, plotBounds.maxZ());
        }
        if (minX > maxX || minY > maxY || minZ > maxZ)
            return;

        BlockPos.MutableBlockPos targetPos = new BlockPos.MutableBlockPos();
        Vector3d targetCenter = new Vector3d();
        Vector3d globalCenter = new Vector3d();
        Vector3d sourceCenter = new Vector3d();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    targetCenter.set(x + 0.5, y + 0.5, z + 0.5);
                    if (targetSubLevel == null)
                        globalCenter.set(targetCenter);
                    else targetSubLevel.logicalPose().transformPosition(targetCenter, globalCenter);
                    if (sourceSubLevel == null)
                        sourceCenter.set(globalCenter);
                    else sourceSubLevel.logicalPose().transformPositionInverse(globalCenter, sourceCenter);
                    if (!sourceBounds.contains(sourceCenter.x, sourceCenter.y, sourceCenter.z))
                        continue;

                    targetPos.set(x, y, z);
                    if (!context.level().isLoaded(targetPos)
                            || !StandardAreaHarvests.harvest(
                                    context, targetPos, context.level().getBlockState(targetPos)))
                        continue;
                    result.add(BlockPos.containing(sourceCenter.x, sourceCenter.y, sourceCenter.z));
                }
            }
        }
    }

    private static final class ParticleReservoir {
        private final ServerLevel level;
        private final List<BlockPos> particleSources;
        private int harvested;

        private ParticleReservoir(ServerLevel level, HarvestResult initial) {
            this.level = level;
            this.harvested = initial.harvested();
            this.particleSources = new ArrayList<>(initial.particleSources());
        }

        private void add(BlockPos sourcePosition) {
            harvested++;
            if (particleSources.size() < VacuumHarvesterHarvesting.MAX_PARTICLE_SOURCES) {
                particleSources.add(sourcePosition.immutable());
                return;
            }
            int replacement = level.random.nextInt(harvested);
            if (replacement < VacuumHarvesterHarvesting.MAX_PARTICLE_SOURCES)
                particleSources.set(replacement, sourcePosition.immutable());
        }

        private HarvestResult build() {
            return new HarvestResult(harvested, List.copyOf(particleSources));
        }
    }
}
