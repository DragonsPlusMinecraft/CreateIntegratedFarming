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

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.animation.AnimationTickHolder;
import plus.dragons.createintegratedfarming.client.CIFPartialModels;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterMovementBehaviour;

public class VacuumHarvesterActorVisual extends ActorVisual {
    private final TransformedInstance moving;

    public VacuumHarvesterActorVisual(
            VisualizationContext visualizationContext,
            VirtualRenderWorld simulationWorld,
            MovementContext movementContext) {
        super(visualizationContext, simulationWorld, movementContext);
        moving = instancerProvider
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(CIFPartialModels.VACUUM_HARVESTER_MOVING))
                .createInstance();
        moving.light(localBlockLight(), 0);
        transform();
    }

    @Override
    public void beginFrame() {
        transform();
    }

    private void transform() {
        moving.setIdentityTransform()
                .translate(context.localPos)
                .translate(
                        0,
                        -VacuumHarvesterMovementBehaviour.getRenderedHeadOffset(
                                context, AnimationTickHolder.getPartialTicks()),
                        0)
                .setChanged();
    }

    @Override
    protected void _delete() {
        moving.delete();
    }
}
