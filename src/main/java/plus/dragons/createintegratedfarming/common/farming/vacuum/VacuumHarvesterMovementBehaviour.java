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

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.client.renderer.VacuumHarvesterActorVisual;
import plus.dragons.createintegratedfarming.client.renderer.VacuumHarvesterRenderer;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class VacuumHarvesterMovementBehaviour implements MovementBehaviour {
    private static final String CYCLE_INITIALIZED = "VacuumCycleInitialized";
    private static final String HEAD_OFFSET = "VacuumHeadOffset";
    private static final String PREVIOUS_HEAD_OFFSET = "VacuumPreviousHeadOffset";

    @Override
    public void tick(MovementContext context) {
        if (context.position == null)
            return;
        initializeCycle(context);
        context.data.putFloat(PREVIOUS_HEAD_OFFSET, context.data.getFloat(HEAD_OFFSET));

        int releaseTicks = context.data.getInt(VacuumHarvesterCycle.RELEASE_TICKS);
        double chargeProgress = context.data.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS);
        if (releaseTicks > 0) {
            releaseTicks--;
            if (releaseTicks == 0)
                chargeProgress = 0;
        } else {
            chargeProgress = VacuumHarvesterCycle.advanceCharge(
                    chargeProgress, VacuumHarvesterCycle.contraptionChargeIncrement());
            if (chargeProgress >= 1) {
                releaseTicks = VacuumHarvesterCycle.RELEASE_DURATION;
                if (!context.world.isClientSide)
                    harvestArea(context);
            }
        }

        float headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);
        context.data.putDouble(VacuumHarvesterCycle.CHARGE_PROGRESS, chargeProgress);
        context.data.putInt(VacuumHarvesterCycle.RELEASE_TICKS, releaseTicks);
        context.data.putFloat(HEAD_OFFSET, headOffset);
        persistCycle(context);

        if (context.world.isClientSide
                && releaseTicks == 0
                && Math.floorMod(context.world.getGameTime() + context.localPos.hashCode(), 8) == 0)
            VacuumHarvesterEffects.spawnExhaust(
                    context.world, VacuumHarvesterEffects.intake(context.position, headOffset));
    }

    private static void initializeCycle(MovementContext context) {
        if (context.data.getBoolean(CYCLE_INITIALIZED))
            return;
        CompoundTag blockEntityData = context.blockEntityData;
        double chargeProgress = blockEntityData == null
                ? 0
                : Mth.clamp(blockEntityData.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS), 0, 1);
        int releaseTicks = blockEntityData == null
                ? 0
                : Mth.clamp(
                        blockEntityData.getInt(VacuumHarvesterCycle.RELEASE_TICKS),
                        0,
                        VacuumHarvesterCycle.RELEASE_DURATION);
        float headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);
        context.data.putBoolean(CYCLE_INITIALIZED, true);
        context.data.putDouble(VacuumHarvesterCycle.CHARGE_PROGRESS, chargeProgress);
        context.data.putInt(VacuumHarvesterCycle.RELEASE_TICKS, releaseTicks);
        context.data.putFloat(HEAD_OFFSET, headOffset);
        context.data.putFloat(PREVIOUS_HEAD_OFFSET, headOffset);
    }

    private static void persistCycle(MovementContext context) {
        if (context.blockEntityData == null)
            context.blockEntityData = new CompoundTag();
        context.blockEntityData.putDouble(
                VacuumHarvesterCycle.CHARGE_PROGRESS,
                context.data.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS));
        context.blockEntityData.putInt(
                VacuumHarvesterCycle.RELEASE_TICKS,
                context.data.getInt(VacuumHarvesterCycle.RELEASE_TICKS));
    }

    private void harvestArea(MovementContext context) {
        BlockPos center = BlockPos.containing(context.position);
        var storage = context.contraption.getStorage().getAllItems();
        AreaHarvestContext harvestContext = new AreaHarvestContext(
                context.world,
                true,
                false,
                CustomHarvestBehaviour.getHarvestTool(context),
                stack -> collectOrDropItem(context, stack),
                (predicate, amount) -> ItemHelper.extract(storage, predicate, amount, false));
        var result = VacuumHarvesterHarvesting.harvestArea(
                harvestContext, center, CIFConfig.server().vacuumHarvesterRange.get());
        VacuumHarvesterEffects.emitSuction(
                (ServerLevel) context.world,
                VacuumHarvesterEffects.intake(context.position, VacuumHarvesterCycle.MAX_HEAD_OFFSET),
                result.particleSources());
    }

    public static float getRenderedHeadOffset(MovementContext context, float partialTicks) {
        initializeCycle(context);
        return Mth.lerp(
                partialTicks,
                context.data.getFloat(PREVIOUS_HEAD_OFFSET),
                context.data.getFloat(HEAD_OFFSET));
    }

    @Override
    public void stopMoving(MovementContext context) {
        initializeCycle(context);
        persistCycle(context);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void renderInContraption(
            MovementContext context,
            VirtualRenderWorld renderWorld,
            ContraptionMatrices matrices,
            MultiBufferSource buffers) {
        if (!VisualizationManager.supportsVisualization(context.world))
            VacuumHarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffers);
    }

    @Nullable
    @Override
    public ActorVisual createVisual(
            VisualizationContext visualizationContext,
            VirtualRenderWorld simulationWorld,
            MovementContext movementContext) {
        return new VacuumHarvesterActorVisual(visualizationContext, simulationWorld, movementContext);
    }
}
