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

package plus.dragons.createintegratedfarming.integration.twilightdelight.ponder;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import twilightforest.init.TFItems;

public class TwilightDelightPonderScenes {
    public static void harvestLilyPads(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("mechanical_arm.harvest_lily_pads", "Harvesting Lily Pads");
        scene.configureBasePlate(0, 0, 7);
        var y01 = scene.world().showIndependentSection(util.select().layers(0, 2).substract(util.select().fromTo(1, 0, 1, 5, 0, 1)), Direction.DOWN);
        Vec3 down = new Vec3(0, -1, 0);
        scene.world().moveSection(y01, down, 0);

        scene.idle(10);
        var lily = scene.world().showIndependentSection(util.select().position(3, 2, 3), Direction.DOWN);
        scene.world().moveSection(lily, down, 0);
        scene.idle(20);
        var rest1 = scene.world().showIndependentSectionImmediately(util.select().fromTo(3, 2, 1, 4, 2, 2).add(util.select().fromTo(2, 2, 4, 3, 2, 5)));
        scene.world().moveSection(rest1, down, 0);
        scene.idle(20);
        var rest2 = scene.world().showIndependentSectionImmediately(util.select().fromTo(1, 2, 1, 2, 2, 2).add(util.select().fromTo(4, 2, 4, 5, 2, 5)));
        scene.world().moveSection(rest2, down, 0);
        scene.idle(10);
        var rest3 = scene.world().showIndependentSectionImmediately(util.select().position(2, 2, 3).add(util.select().position(4, 2, 3)));
        scene.world().moveSection(rest3, down, 0);
        scene.idle(10);
        var rest4 = scene.world().showIndependentSectionImmediately(util.select().position(1, 2, 3).add(util.select().position(5, 2, 3)));
        scene.world().moveSection(rest4, down, 0);
        scene.idle(5);
        var appliance = scene.world().showIndependentSectionImmediately(util.select().position(4, 2, 0).add(util.select().position(2, 2, 0)));
        scene.world().moveSection(appliance, down, 0);

        BlockPos target = util.grid().at(3, 1, 3);
        BlockPos output = util.grid().at(2, 1, 0);
        scene.overlay().showText(60)
                .text("Mechanical Arms can target Huge Water Lilies")
                .pointAt(util.vector().centerOf(target))
                .placeNearTarget()
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.INPUT, target, util.select().position(target), 60);
        scene.overlay().showOutline(PonderPalette.OUTPUT, output, util.select().position(output), 60);
        scene.idle(70);

        scene.overlay().showText(80)
                .text("The arm scans the four adjacent water-surface blocks, not the target itself")
                .pointAt(util.vector().centerOf(target))
                .placeNearTarget()
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.INPUT, target, util.select().fromTo(2, 1, 2, 4, 1, 4), 80);
        scene.overlay().showOutline(PonderPalette.OUTPUT, output, util.select().position(output), 80);
        scene.idle(90);

        var armPos = util.grid().at(4, 2, 0);
        scene.overlay().showText(80)
                .text("Lily Pads, Huge Lily Pads, and nearby Huge Water Lilies can be collected while the target stays in place")
                .pointAt(util.vector().centerOf(target))
                .placeNearTarget()
                .attachKeyFrame();
        scene.world().setKineticSpeed(util.select().position(armPos), 64);
        var hugeLilyPadStack = TFItems.HUGE_LILY_PAD.toStack();
        var hugeWaterLilyStack = TFItems.HUGE_WATER_LILY.toStack();
        var outputDepot = util.grid().at(2, 2, 0);

        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().replaceBlocks(util.select().fromTo(3, 2, 1, 4, 2, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, hugeLilyPadStack, -1);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, hugeLilyPadStack, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(outputDepot, DepotBlockEntity.class, depot -> depot.setHeldItem(hugeLilyPadStack));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, -1);
        scene.idle(20);

        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().replaceBlocks(util.select().position(4, 2, 3), Blocks.AIR.defaultBlockState(), false);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, hugeWaterLilyStack, -1);
        scene.world().modifyBlockEntity(outputDepot, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, hugeWaterLilyStack, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(outputDepot, DepotBlockEntity.class, depot -> depot.setHeldItem(hugeWaterLilyStack));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, -1);
        scene.idle(40);
    }
}
