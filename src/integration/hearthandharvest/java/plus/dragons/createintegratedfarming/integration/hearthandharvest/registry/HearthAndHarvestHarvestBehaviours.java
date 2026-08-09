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

package plus.dragons.createintegratedfarming.integration.hearthandharvest.registry;

import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest.BerryHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest.CornHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest.CottonHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.hearthandharvest.farming.harvest.GrapeHarvestBehaviour;

public class HearthAndHarvestHarvestBehaviours {
    private static final BerryHarvestBehaviour BLUEBERRY = new BerryHarvestBehaviour(
            HHModBlocks.BLUEBERRY_BUSH, HHModItems.BLUEBERRIES);
    private static final BerryHarvestBehaviour RASPBERRY = new BerryHarvestBehaviour(
            HHModBlocks.RASPBERRY_BUSH, HHModItems.RASPBERRY);
    private static final CornHarvestBehaviour CORN = new CornHarvestBehaviour();
    private static final CottonHarvestBehaviour COTTON = new CottonHarvestBehaviour();
    private static final GrapeHarvestBehaviour GRAPE = new GrapeHarvestBehaviour();

    public static void register() {
        CustomHarvestBehaviour.REGISTRY.registerProvider(HearthAndHarvestHarvestBehaviours::createBerry);
        CustomHarvestBehaviour.REGISTRY.registerProvider(HearthAndHarvestHarvestBehaviours::createCorn);
        CustomHarvestBehaviour.REGISTRY.registerProvider(HearthAndHarvestHarvestBehaviours::createCotton);
        CustomHarvestBehaviour.REGISTRY.registerProvider(HearthAndHarvestHarvestBehaviours::createGrape);
    }

    private static @Nullable CustomHarvestBehaviour createBerry(Block block) {
        if (block == HHModBlocks.BLUEBERRY_BUSH.get())
            return BLUEBERRY;
        if (block == HHModBlocks.RASPBERRY_BUSH.get())
            return RASPBERRY;
        return null;
    }

    private static @Nullable CustomHarvestBehaviour createCorn(Block block) {
        return block == HHModBlocks.CORN_STALK.get() ? CORN : null;
    }

    private static @Nullable CustomHarvestBehaviour createCotton(Block block) {
        return block == HHModBlocks.COTTON_CROP.get() ? COTTON : null;
    }

    private static @Nullable CustomHarvestBehaviour createGrape(Block block) {
        return block == HHModBlocks.GRAPE_TRELLIS.get() ? GRAPE : null;
    }
}
