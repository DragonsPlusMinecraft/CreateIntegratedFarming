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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.ponder;

import com.scouter.netherdepthsupgrade.entity.NDUEntity;
import com.scouter.netherdepthsupgrade.items.NDUItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public class NDUPonderScenes {
    public static void lavaFishing(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("lava_fishing_net", "Using Lava Fishing Net on Contraptions");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.76f);
        var contraptionSelection = util.select().fromTo(0, 1, 5, 3, 3, 5)
                .add(util.select().position(4, 2, 5));
        scene.world().showSection(util.select().everywhere().substract(contraptionSelection).substract(util.select().fromTo(0, 0, 6, 5, 3, 7)), Direction.DOWN);
        ElementLink<WorldSectionElement> fillSpaceLava = scene.world().showIndependentSection(util.select().fromTo(0, 1, 6, 4, 3, 6), Direction.DOWN);
        ElementLink<WorldSectionElement> fillSpaceLava2 = scene.world().showIndependentSection(util.select().fromTo(4, 1, 7, 4, 3, 7), Direction.DOWN);
        scene.world().moveSection(fillSpaceLava, util.vector().of(0, 0, -1), 0);
        scene.world().moveSection(fillSpaceLava2, util.vector().of(0, 0, -2), 0);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(contraptionSelection, Direction.DOWN);
        scene.idle(10);

        scene.world().configureCenterOfRotation(contraption, util.vector().centerOf(4, 3, 5));
        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 5), Direction.NORTH))
                .text("Whenever Lava Fishing Nets are moved as part of an animated Contraption...");
        scene.idle(70);

        scene.world().rotateBearing(util.grid().at(4, 3, 5), -360, 140);
        scene.world().rotateSection(contraption, 0, -360, 0, 140);
        scene.overlay().showText(100)
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 5), Direction.EAST))
                .text("They will fish in lava, like a player fishing with a Lava Fishing Rod")
                .placeNearTarget();
        scene.idle(140);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 5), Direction.NORTH))
                .text("Lava Fishing Net will catch small animal in lava");
        scene.world().hideSection(util.select().fromTo(0, 1, 0, 5, 3, 2), Direction.NORTH);
        var salmon = scene.world().createEntity(level -> {
            var fish = NDUEntity.OBSIDIAN_FISH.get().create(level);
            assert fish != null;
            var pos = util.vector().centerOf(2, 1, 1);
            fish.setPos(pos.x, pos.y, pos.z);
            return fish;
        });
        scene.idle(10);

        scene.world().rotateBearing(util.grid().at(4, 3, 5), -360, 140);
        scene.world().rotateSection(contraption, 0, -360, 0, 140);
        scene.idle(20);
        scene.world().modifyEntity(salmon, Entity::discard);
        scene.idle(120);
        scene.overlay().showControls(util.vector().centerOf(0, 2, 5), Pointing.UP, 40).rightClick()
                .withItem(NDUItems.OBSIDIANFISH.toStack());
        scene.idle(40);
    }
}
