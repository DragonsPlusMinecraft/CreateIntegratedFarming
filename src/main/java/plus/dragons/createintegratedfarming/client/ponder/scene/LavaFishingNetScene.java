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

package plus.dragons.createintegratedfarming.client.ponder.scene;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.List;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.common.CIFCommon;

public class LavaFishingNetScene {
    public static void fishing(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("lava_fishing_net", "Using Lava Fishing Net on Contraptions");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.76F);
        var contraptionSelection = util.select()
                .fromTo(0, 1, 5, 3, 3, 5)
                .add(util.select().position(4, 2, 5));
        scene.world()
                .showSection(
                        util.select()
                                .everywhere()
                                .substract(contraptionSelection)
                                .substract(util.select().fromTo(0, 0, 6, 5, 3, 7)),
                        Direction.DOWN);
        ElementLink<WorldSectionElement> fillSpaceLava = scene.world()
                .showIndependentSection(util.select().fromTo(0, 1, 6, 4, 3, 6), Direction.DOWN);
        ElementLink<WorldSectionElement> fillSpaceLava2 = scene.world()
                .showIndependentSection(util.select().fromTo(4, 1, 7, 4, 3, 7), Direction.DOWN);
        scene.world().moveSection(fillSpaceLava, util.vector().of(0, 0, -1), 0);
        scene.world().moveSection(fillSpaceLava2, util.vector().of(0, 0, -2), 0);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(contraptionSelection, Direction.DOWN);
        scene.idle(10);

        scene.world().configureCenterOfRotation(contraption, util.vector().centerOf(4, 3, 5));
        scene.overlay()
                .showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 5), Direction.NORTH))
                .text("Whenever Lava Fishing Nets are moved as part of an animated Contraption...");
        scene.idle(70);

        scene.world().rotateBearing(util.grid().at(4, 3, 5), -360, 140);
        scene.world().rotateSection(contraption, 0, -360, 0, 140);
        scene.overlay()
                .showText(100)
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 5), Direction.EAST))
                .text("They draw catches from lava fishing pools provided by installed mods")
                .placeNearTarget();
        scene.idle(140);

        ExampleState example = new ExampleState(FishingNetPonderExamples.shuffled());
        scene.overlay()
                .showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 5), Direction.NORTH))
                .text("One compatible lava catch is shown here in its entity or item form");
        scene.world().hideSection(util.select().fromTo(0, 1, 0, 5, 3, 2), Direction.NORTH);
        Vec3 entityPosition = util.vector().centerOf(2, 1, 1);
        var entity = scene.world().createEntity(level -> example.create(level, entityPosition));
        scene.idle(10);

        scene.world().rotateBearing(util.grid().at(4, 3, 5), -360, 140);
        scene.world().rotateSection(contraption, 0, -360, 0, 140);
        scene.idle(20);
        scene.world().modifyEntity(entity, Entity::discard);
        scene.idle(120);
        Vec3 iconPosition = util.vector().centerOf(0, 2, 5);
        scene.debug().enqueueCallback(ponderScene -> example.showIcon(ponderScene, iconPosition));
        scene.idle(40);
        scene.debug().enqueueCallback(ponderScene -> example.hideIcon());
    }

    private static class ExampleState {
        private final List<FishingNetPonderExample> candidates;
        private @Nullable ItemStack icon;
        private @Nullable InputWindowElement iconElement;

        private ExampleState(List<FishingNetPonderExample> candidates) {
            this.candidates = candidates;
        }

        private Entity create(Level level, Vec3 position) {
            for (FishingNetPonderExample candidate : candidates) {
                try {
                    ItemStack candidateIcon = candidate.iconSupplier().get();
                    if (candidateIcon == null || candidateIcon.isEmpty())
                        continue;
                    Entity entity = candidate.entityFactory().apply(level);
                    if (entity == null)
                        continue;
                    entity.setPos(position);
                    icon = candidateIcon.copy();
                    return entity;
                } catch (RuntimeException exception) {
                    CIFCommon.LOGGER.warn("Could not create fishing net Ponder example {}", candidate.id(), exception);
                }
            }
            ItemEntity placeholder = new ItemEntity(level, position.x, position.y, position.z, ItemStack.EMPTY);
            placeholder.setInvisible(true);
            return placeholder;
        }

        private void showIcon(PonderScene scene, Vec3 position) {
            if (icon == null || icon.isEmpty())
                return;
            iconElement = new InputWindowElement(position, Pointing.UP);
            iconElement.builder().rightClick().withItem(icon);
            iconElement.setVisible(true);
            iconElement.setFade(1.0F);
            scene.addElement(iconElement);
        }

        private void hideIcon() {
            if (iconElement != null)
                iconElement.setVisible(false);
        }
    }
}
