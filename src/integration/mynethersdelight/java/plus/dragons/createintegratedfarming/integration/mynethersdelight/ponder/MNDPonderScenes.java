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

package plus.dragons.createintegratedfarming.integration.mynethersdelight.ponder;

import static com.soytutta.mynethersdelight.common.block.LetiosCompostBlock.FORGOTING;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.soytutta.mynethersdelight.common.registry.MNDBlocks;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MNDPonderScenes {
    public static void chargingSoil(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spout.catalyze_letios_compost", "Catalyzing Leteos Compost");
        scene.configureBasePlate(0, 0, 3);
        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        var spout = util.select().position(1, 3, 1);
        var leteosCompost = util.grid().at(1, 1, 1);

        scene.overlay().showText(100)
                .text("Forgetting process of Leteos Compost can be speed up via Spout in ultra warm dimension")
                .pointAt(util.vector().centerOf(1, 3, 1))
                .placeNearTarget();

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 2), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 4), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 7), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 9), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 9), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> MNDBlocks.RESURGENT_SOIL.get().defaultBlockState(), false);
    }

    public static void harvestPowderyCrops(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("mechanical_arm.harvest_powdery_crops", "Harvesting Powdery Crops");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos cropBase = util.grid().at(2, 1, 2);
        BlockPos ripeCrop = util.grid().at(2, 5, 2);
        BlockPos armPos = util.grid().at(1, 1, 3);
        BlockPos outputDepot = util.grid().at(3, 1, 3);
        var cropColumn = util.select().fromTo(cropBase, ripeCrop);
        var arm = util.select().position(armPos);
        var output = util.select().position(outputDepot);
        var armAndOutput = util.select().position(armPos).add(output);

        scene.world().showSection(cropColumn, Direction.UP);
        scene.idle(10);

        scene.overlay().showText(60)
                .text("Mechanical Arms can harvest Powdery Crops")
                .pointAt(util.vector().centerOf(armPos))
                .placeNearTarget()
                .attachKeyFrame();
        scene.world().showSection(armAndOutput, Direction.DOWN);
        scene.idle(70);

        scene.overlay().showText(70)
                .text("The arm scans up to 7 blocks upward from the crop block selected as its target")
                .pointAt(util.vector().centerOf(cropBase))
                .placeNearTarget()
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.INPUT, cropBase, util.select().position(cropBase), 40);
        scene.overlay().showOutline(PonderPalette.OUTPUT, output, output, 90);
        scene.idle(50);
        scene.overlay().showOutline(PonderPalette.INPUT, cropColumn, cropColumn, 40);
        scene.idle(40);

        scene.overlay().showText(70)
                .text("When a ripe part is found, it collects Bullet Peppers without breaking the plant")
                .pointAt(util.vector().centerOf(ripeCrop))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(10);
        scene.world().setKineticSpeed(arm, 64);
        var pepper = new ItemStack(MNDItems.BULLET_PEPPER.get());
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().modifyBlock(ripeCrop, state -> state.hasProperty(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : state, false);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, pepper, -1);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, pepper, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(outputDepot, DepotBlockEntity.class, depot -> depot.setHeldItem(pepper));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, -1);
    }
}
