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

package plus.dragons.createintegratedfarming.integration.nethersexoticism.registry;

import net.mcreator.nethersexoticism.init.NethersExoticismModBlocks;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest.BouddhasHandHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest.JaboticabaHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest.KiwanoHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest.PitayaHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.nethersexoticism.farming.harvest.RambutanHarvestBehaviour;

public class NethersExoticismHarvestBehaviours {
    private static final BouddhasHandHarvestBehaviour BOUDDHAS_HAND = new BouddhasHandHarvestBehaviour();
    private static final JaboticabaHarvestBehaviour JABOTICABA = new JaboticabaHarvestBehaviour();
    private static final KiwanoHarvestBehaviour KIWANO = new KiwanoHarvestBehaviour();
    private static final PitayaHarvestBehaviour PITAYA = new PitayaHarvestBehaviour();
    private static final RambutanHarvestBehaviour RAMBUTAN = new RambutanHarvestBehaviour();

    public static void register() {
        CustomHarvestBehaviour.REGISTRY.registerProvider(NethersExoticismHarvestBehaviours::createBouddhasHand);
        CustomHarvestBehaviour.REGISTRY.registerProvider(NethersExoticismHarvestBehaviours::createJaboticaba);
        CustomHarvestBehaviour.REGISTRY.registerProvider(NethersExoticismHarvestBehaviours::createKiwano);
        CustomHarvestBehaviour.REGISTRY.registerProvider(NethersExoticismHarvestBehaviours::createPitaya);
        CustomHarvestBehaviour.REGISTRY.registerProvider(NethersExoticismHarvestBehaviours::createRambutan);
    }

    private static @Nullable CustomHarvestBehaviour createBouddhasHand(Block block) {
        return block == NethersExoticismModBlocks.BOUDDHA_S_HAND_BLOCK.get() ? BOUDDHAS_HAND : null;
    }

    private static @Nullable CustomHarvestBehaviour createJaboticaba(Block block) {
        return block == NethersExoticismModBlocks.JABOTICABA_BRANCH.get() ? JABOTICABA : null;
    }

    private static @Nullable CustomHarvestBehaviour createKiwano(Block block) {
        return block == NethersExoticismModBlocks.KIWANO_LEAVES_STAGE_1.get() ? KIWANO : null;
    }

    private static @Nullable CustomHarvestBehaviour createPitaya(Block block) {
        return block == NethersExoticismModBlocks.PITAYA_BLOCK.get() ||
                block == NethersExoticismModBlocks.PITAYA_BLOCK_OPEN.get() ? PITAYA : null;
    }

    private static @Nullable CustomHarvestBehaviour createRambutan(Block block) {
        return block == NethersExoticismModBlocks.RAMBOUTAN_BLOCK.get() ? RAMBUTAN : null;
    }
}
