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

package plus.dragons.createintegratedfarming.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import plus.dragons.createintegratedfarming.client.CIFPartialModels;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlockEntity;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterMovementBehaviour;

public class VacuumHarvesterRenderer extends SafeBlockEntityRenderer<VacuumHarvesterBlockEntity> {
    public VacuumHarvesterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(
            VacuumHarvesterBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            int overlay) {
        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;
        SuperByteBuffer moving = CachedBuffers.partial(
                CIFPartialModels.VACUUM_HARVESTER_MOVING, blockEntity.getBlockState());
        moving.translate(0, -blockEntity.getRenderedHeadOffset(partialTicks), 0)
                .light(light)
                .renderInto(poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()));
    }

    public static void renderInContraption(
            MovementContext context,
            VirtualRenderWorld renderWorld,
            ContraptionMatrices matrices,
            MultiBufferSource bufferSource) {
        SuperByteBuffer moving = CachedBuffers.partial(CIFPartialModels.VACUUM_HARVESTER_MOVING, context.state);
        moving.transform(matrices.getModel())
                .translate(
                        0,
                        -VacuumHarvesterMovementBehaviour.getRenderedHeadOffset(
                                context, AnimationTickHolder.getPartialTicks()),
                        0)
                .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), bufferSource.getBuffer(RenderType.cutoutMipped()));
    }
}
