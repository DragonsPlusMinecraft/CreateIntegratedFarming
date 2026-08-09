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

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.List;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlockEntity;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterCycle;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterEffects;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

public class VacuumHarvesterScene {
    private static final AirParticleData EXHAUST = new AirParticleData(1.0F, 0.08F);
    private static final AirParticleData SUCTION = new AirParticleData(2.0F, 0.35F);

    public static void operate(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("vacuum_harvester.operate", "Charging and Harvesting Crops");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos machine = util.grid().at(2, 1, 2);
        BlockPos shaft = util.grid().at(2, 0, 2);
        BlockPos cog = util.grid().at(2, 0, 3);
        List<BlockPos> crops = List.of(
                util.grid().at(1, 1, 1),
                util.grid().at(3, 1, 1),
                util.grid().at(1, 1, 3),
                util.grid().at(3, 1, 3));
        Selection farmland = util.select()
                .position(1, 0, 1)
                .add(util.select().position(3, 0, 1))
                .add(util.select().position(1, 0, 3))
                .add(util.select().position(3, 0, 3));
        Selection cropSelection = positions(util, crops);

        scene.world().setBlocks(farmland, Blocks.FARMLAND.defaultBlockState(), false);
        scene.world().setBlock(crops.get(0), mature(Blocks.WHEAT), false);
        scene.world().setBlock(crops.get(1), mature(Blocks.CARROTS), false);
        scene.world().setBlock(crops.get(2), mature(Blocks.POTATOES), false);
        scene.world().setBlock(crops.get(3), mature(Blocks.WHEAT), false);
        scene.world().setBlock(machine, CIFBlocks.VACUUM_HARVESTER.getDefaultState(), false);
        scene.world().setBlock(
                shaft,
                AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y),
                false);
        scene.world().setBlock(
                cog,
                AllBlocks.COGWHEEL.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y),
                false);
        scene.world().showSection(farmland.add(cropSelection), Direction.UP);
        Selection kinetics = positions(util, List.of(machine, shaft, cog));
        scene.world().showSection(kinetics, Direction.DOWN);
        scene.world().setKineticSpeed(kinetics, 64);
        scene.world().modifyBlockEntity(
                machine, VacuumHarvesterBlockEntity.class, blockEntity -> blockEntity.setCycleProgress(0.75F, 0));
        scene.idle(20);

        scene.overlay().showText(80)
                .text("Rotational power charges the chamber; by default, 64 RPM takes 30 seconds")
                .pointAt(util.vector().topOf(machine))
                .placeNearTarget();
        emitExhaust(
                scene,
                VacuumHarvesterEffects.intake(
                        util.vector().centerOf(machine), VacuumHarvesterCycle.getHeadOffset(0.75F, 0)),
                60);
        scene.idle(70);

        scene.world().modifyBlockEntity(
                machine, VacuumHarvesterBlockEntity.class, blockEntity -> blockEntity.setCycleProgress(0.98F, 0));
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Higher RPM fills the chamber faster while using proportionally more stress")
                .pointAt(util.vector().blockSurface(machine, Direction.DOWN))
                .placeNearTarget();
        scene.idle(55);

        scene.world().modifyBlockEntity(
                machine,
                VacuumHarvesterBlockEntity.class,
                blockEntity -> blockEntity.setCycleProgress(1, VacuumHarvesterCycle.RELEASE_DURATION));
        Vec3 intake = VacuumHarvesterEffects.intake(
                util.vector().centerOf(machine), VacuumHarvesterCycle.MAX_HEAD_OFFSET);
        scene.overlay().showOutline(PonderPalette.OUTPUT, cropSelection, cropSelection, 50);
        emitSuctionBurst(scene, util, crops, intake);
        for (BlockPos crop : crops) {
            scene.world().modifyBlock(crop, state -> state.setValue(CropBlock.AGE, 0), true);
        }
        scene.effects().indicateSuccess(machine);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("At full pressure it rises, drawing in every mature crop in the current area at once")
                .pointAt(intake)
                .placeNearTarget();
        scene.idle(80);

        BlockPos funnel = util.grid().at(1, 1, 2);
        BlockPos depot = util.grid().at(1, 0, 2);
        scene.world().setBlock(
                funnel,
                AllBlocks.BRASS_FUNNEL.getDefaultState()
                        .setValue(FunnelBlock.FACING, Direction.WEST)
                        .setValue(FunnelBlock.EXTRACTING, true),
                false);
        scene.world().setBlock(depot, AllBlocks.DEPOT.getDefaultState(), false);
        scene.world().showSection(positions(util, List.of(funnel, depot)), Direction.EAST);
        scene.world().modifyBlockEntity(
                machine,
                VacuumHarvesterBlockEntity.class,
                blockEntity -> blockEntity.getInventory().insertItem(0, new ItemStack(Items.WHEAT, 4), false));
        scene.idle(15);
        scene.world().flapFunnel(funnel, false);
        scene.world().modifyBlockEntity(
                depot, DepotBlockEntity.class, blockEntity -> blockEntity.setHeldItem(new ItemStack(Items.WHEAT)));
        scene.overlay().showText(70)
                .text("Harvests are stored internally and can be extracted from any side except the shaft side")
                .pointAt(util.vector().centerOf(funnel))
                .placeNearTarget();
        scene.idle(80);
    }

    public static void contraption(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("vacuum_harvester.contraption", "Using a Vacuum Harvester on Contraptions");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();

        List<BlockPos> crops = List.of(
                util.grid().at(1, 1, 1),
                util.grid().at(2, 1, 1),
                util.grid().at(1, 1, 2),
                util.grid().at(2, 1, 2));
        Selection farmland = util.select().fromTo(1, 0, 1, 2, 0, 2);
        Selection cropSelection = positions(util, crops);
        scene.world().setBlocks(farmland, Blocks.FARMLAND.defaultBlockState(), false);
        for (BlockPos crop : crops)
            scene.world().setBlock(crop, mature(Blocks.WHEAT), false);
        scene.world().showSection(farmland.add(cropSelection), Direction.UP);

        BlockPos bearing = util.grid().at(4, 1, 4);
        BlockPos machine = util.grid().at(1, 1, 4);
        BlockPos chest = util.grid().at(2, 1, 4);
        BlockPos controls = util.grid().at(3, 1, 4);
        Selection chassis = util.select().fromTo(1, 2, 4, 4, 2, 4);
        Selection bearingSelection = util.select().position(bearing);
        Selection assemblySelection = chassis.add(util.select().fromTo(machine, controls));

        scene.world().setBlock(
                bearing,
                AllBlocks.MECHANICAL_BEARING.getDefaultState()
                        .setValue(BlockStateProperties.FACING, Direction.UP),
                false);
        scene.world().setBlock(machine, CIFBlocks.VACUUM_HARVESTER.getDefaultState(), false);
        scene.world().setBlocks(
                chassis,
                AllBlocks.LINEAR_CHASSIS.getDefaultState()
                        .setValue(BlockStateProperties.AXIS, Direction.Axis.X),
                false);
        scene.world().setBlock(chest, Blocks.BARREL.defaultBlockState(), false);
        scene.world().setBlock(controls, AllBlocks.CONTRAPTION_CONTROLS.getDefaultState(), false);
        scene.world().setFilterData(
                util.select().position(controls),
                ContraptionControlsBlockEntity.class,
                CIFBlocks.VACUUM_HARVESTER.asStack());
        scene.world().showSection(bearingSelection, Direction.DOWN);
        scene.idle(8);
        ElementLink<WorldSectionElement> assembly = scene.world().showIndependentSection(assemblySelection, Direction.DOWN);
        scene.world().configureCenterOfRotation(assembly, util.vector().centerOf(bearing));
        scene.effects().superGlue(machine.above(), Direction.DOWN, true);
        scene.effects().superGlue(chest.above(), Direction.DOWN, true);
        scene.effects().superGlue(controls.above(), Direction.DOWN, true);
        scene.world().modifyBlockEntity(
                machine, VacuumHarvesterBlockEntity.class, blockEntity -> blockEntity.setCycleProgress(0.55F, 0));
        scene.idle(20);

        scene.overlay().showText(70)
                .text("When assembled, the chamber charges on its own fixed timer")
                .pointAt(util.vector().centerOf(machine))
                .placeNearTarget();
        scene.world().setKineticSpeed(bearingSelection, -32);
        scene.world().rotateBearing(bearing, -90, 50);
        scene.world().rotateSection(assembly, 0, -90, 0, 50);
        scene.idle(60);
        scene.world().setKineticSpeed(bearingSelection, 0);

        Vec3 movedMachineCenter = util.vector().centerOf(4, 1, 1);
        Vec3 movedMachine = VacuumHarvesterEffects.intake(
                movedMachineCenter, VacuumHarvesterCycle.getHeadOffset(0.92F, 0));
        scene.overlay().showText(75)
                .attachKeyFrame()
                .text("The timer continues even while the assembled Contraption is standing still")
                .pointAt(movedMachine)
                .placeNearTarget();
        scene.world().modifyBlockEntity(
                machine, VacuumHarvesterBlockEntity.class, blockEntity -> blockEntity.setCycleProgress(0.92F, 0));
        emitExhaust(scene, movedMachine, 55);
        scene.idle(65);

        scene.world().modifyBlockEntity(
                machine,
                VacuumHarvesterBlockEntity.class,
                blockEntity -> blockEntity.setCycleProgress(1, VacuumHarvesterCycle.RELEASE_DURATION));
        movedMachine = VacuumHarvesterEffects.intake(
                movedMachineCenter, VacuumHarvesterCycle.MAX_HEAD_OFFSET);
        scene.overlay().showOutline(PonderPalette.OUTPUT, cropSelection, cropSelection, 50);
        emitSuctionBurst(scene, util, crops, movedMachine);
        for (BlockPos crop : crops) {
            scene.world().modifyBlock(crop, state -> state.setValue(CropBlock.AGE, 0), true);
        }
        scene.overlay().showText(75)
                .text("Each pulse harvests the complete area around its current position, not only newly entered blocks")
                .pointAt(movedMachine)
                .placeNearTarget();
        scene.idle(85);

        Vec3 movedControls = util.vector().centerOf(4, 1, 3);
        scene.overlay().showControls(movedControls, Pointing.DOWN, 25).rightClick();
        scene.world().modifyBlockEntity(
                controls, ContraptionControlsBlockEntity.class, blockEntity -> blockEntity.disabled = true);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Contraption Controls pause charging until the actor is enabled again")
                .pointAt(movedControls)
                .placeNearTarget();
        scene.idle(80);
    }

    private static net.minecraft.world.level.block.state.BlockState mature(
            net.minecraft.world.level.block.Block block) {
        return block.defaultBlockState().setValue(CropBlock.AGE, 7);
    }

    private static Selection positions(SceneBuildingUtil util, List<BlockPos> positions) {
        Selection selection = util.select().position(positions.getFirst());
        for (int i = 1; i < positions.size(); i++)
            selection = selection.add(util.select().position(positions.get(i)));
        return selection;
    }

    private static void emitExhaust(CreateSceneBuilder scene, Vec3 intake, int ticks) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Vec3 target = intake.add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.65)).add(0, 0.15, 0);
            emitAir(scene, intake, target, EXHAUST, ticks);
        }
    }

    private static void emitSuctionBurst(
            CreateSceneBuilder scene, SceneBuildingUtil util, List<BlockPos> crops, Vec3 intake) {
        for (int i = 0; i < 32; i++) {
            double angle = i * Math.PI * 2 / 32;
            double radius = 1.75 + (i % 4) * 0.2;
            double height = (i % 7 - 3) * 0.12;
            Vec3 source = intake.add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            emitAir(scene, source, intake, SUCTION, 1);
        }
        for (BlockPos crop : crops) {
            Vec3 center = util.vector().centerOf(crop).add(0, 0.25, 0);
            emitAir(scene, center.add(0.22, 0.1, -0.18), intake, SUCTION, 1);
            emitAir(scene, center.add(-0.2, 0.3, 0.2), intake, SUCTION, 1);
        }
    }

    private static void emitAir(
            CreateSceneBuilder scene,
            Vec3 source,
            Vec3 target,
            AirParticleData particle,
            int cycles) {
        scene.effects().emitParticles(
                source,
                scene.effects().simpleParticleEmitter(particle, target.subtract(source)),
                cycles > 1 ? 0.125F : 1,
                cycles);
    }
}
