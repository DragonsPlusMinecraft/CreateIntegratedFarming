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

package plus.dragons.createintegratedfarming.integration.farmersdelight.ponder;

import static vectorwing.farmersdelight.common.block.OrganicCompostBlock.COMPOSTING;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class FDPonderScenes {
    public static void catalyze(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spout.catalyze_organic_compost", "Catalyzing Organic Compost");
        scene.configureBasePlate(0, 0, 3);

        scene.world().modifyBlockEntity(util.grid().at(1, 3, 1), SpoutBlockEntity.class, be -> {
            var tank = be.getBehaviour(SmartFluidTankBehaviour.TYPE);
            tank.getPrimaryHandler().setFluid(new FluidStack(Fluids.WATER, 1000));
        });
        scene.world().showSection(util.select().everywhere(), Direction.DOWN);

        var spout = util.select().position(1, 3, 1);
        var compost = util.grid().at(1, 1, 1);

        scene.overlay().showText(100)
                .text("Degradation process of Organic Compost can be speed up via Spout")
                .pointAt(util.vector().centerOf(1, 3, 1))
                .placeNearTarget();

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(compost, bs -> bs.setValue(COMPOSTING, 1), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(compost, bs -> bs.setValue(COMPOSTING, 3), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(compost, bs -> bs.setValue(COMPOSTING, 5), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(compost, bs -> bs.setValue(COMPOSTING, 7), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(compost, bs -> ModBlocks.RICH_SOIL.get().defaultBlockState(), false);
    }
}
