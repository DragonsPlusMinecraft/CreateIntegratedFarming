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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.registry;

import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import plus.dragons.createintegratedfarming.integration.netherdepthupgrade.content.fishing.net.LavaFishingNetBlock;
import plus.dragons.createintegratedfarming.integration.netherdepthupgrade.content.fishing.net.LavaFishingNetMovementBehaviour;

public class NDUBlocks {
    public static final BlockEntry<LavaFishingNetBlock> LAVA_FISHING_NET = REGISTRATE
            .block("lava_fishing_net", LavaFishingNetBlock::new)
            .lang("Lava Fishing Net")
            .initialProperties(SharedProperties::softMetal)
            .properties(prop -> {
                var result = prop.mapColor(MapColor.METAL)
                        .sound(SoundType.CHAIN)
                        .noOcclusion();
                // Do not datagen loot table since loot table data provider doesn't implement load conditions. We should write this loot table manually.
                return DatagenModLoader.isRunningDataGen() ? result.noLootTable() : result;
            })
            .asOptional()
            .transform(axeOnly())
            .tag(AllTags.AllBlockTags.WINDMILL_SAILS.tag)
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .onRegister(block -> MovementBehaviour.REGISTRY.register(block, new LavaFishingNetMovementBehaviour()))
            .simpleItem()
            .register();

    public static void register() {}
}
