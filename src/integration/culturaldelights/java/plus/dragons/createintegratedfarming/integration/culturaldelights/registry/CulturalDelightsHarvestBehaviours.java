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

package plus.dragons.createintegratedfarming.integration.culturaldelights.registry;

import com.baisylia.culturaldelights.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.culturaldelights.farming.harvest.AvocadoHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.culturaldelights.farming.harvest.CornHarvestBehaviour;

public class CulturalDelightsHarvestBehaviours {
    private static final AvocadoHarvestBehaviour AVOCADO = new AvocadoHarvestBehaviour();
    private static final CornHarvestBehaviour CORN = new CornHarvestBehaviour();

    public static void register() {
        CustomHarvestBehaviour.REGISTRY.registerProvider(CulturalDelightsHarvestBehaviours::createAvocado);
        CustomHarvestBehaviour.REGISTRY.registerProvider(CulturalDelightsHarvestBehaviours::createCorn);
    }

    private static @Nullable CustomHarvestBehaviour createAvocado(Block block) {
        return block == ModBlocks.FRUITING_AVOCADO_LEAVES.get() ? AVOCADO : null;
    }

    private static @Nullable CustomHarvestBehaviour createCorn(Block block) {
        return block == ModBlocks.CORN.get() || block == ModBlocks.CORN_UPPER.get() ? CORN : null;
    }
}
