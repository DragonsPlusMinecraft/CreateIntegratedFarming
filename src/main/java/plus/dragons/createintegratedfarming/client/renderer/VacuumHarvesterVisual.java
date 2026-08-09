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

import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import plus.dragons.createintegratedfarming.client.CIFPartialModels;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlockEntity;

public class VacuumHarvesterVisual extends KineticBlockEntityVisual<VacuumHarvesterBlockEntity>
        implements SimpleDynamicVisual {
    private final TransformedInstance moving;

    public VacuumHarvesterVisual(
            VisualizationContext context, VacuumHarvesterBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        moving = instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, Models.partial(CIFPartialModels.VACUUM_HARVESTER_MOVING))
                .createInstance();
        transform(partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        transform(context.partialTick());
    }

    private void transform(float partialTick) {
        moving.setIdentityTransform()
                .translate(getVisualPosition())
                .translate(0, -blockEntity.getRenderedHeadOffset(partialTick), 0)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(moving);
    }

    @Override
    protected void _delete() {
        moving.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(moving);
    }
}
