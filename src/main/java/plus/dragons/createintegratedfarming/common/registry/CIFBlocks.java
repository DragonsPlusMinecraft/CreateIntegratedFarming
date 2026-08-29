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

package plus.dragons.createintegratedfarming.common.registry;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;

import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlock;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterMovementBehaviour;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetBlock;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMovementBehaviour;
import plus.dragons.createintegratedfarming.common.fishing.net.LavaFishingNetBlock;
import plus.dragons.createintegratedfarming.common.fishing.net.LavaFishingNetMovementBehaviour;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlock;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlockItem;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenRoostBlock;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class CIFBlocks {
    public static final BlockEntry<FishingNetBlock> FISHING_NET = REGISTRATE
            .block("fishing_net", FishingNetBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(prop -> prop
                    .mapColor(DyeColor.BROWN)
                    .sound(SoundType.SCAFFOLDING)
                    .noOcclusion())
            .transform(axeOnly())
            .tag(AllBlockTags.WINDMILL_SAILS.tag)
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .onRegister(block -> MovementBehaviour.REGISTRY.register(block, new FishingNetMovementBehaviour()))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> LAVA_FISHING_NET = new BlockEntry<>(REGISTRATE,
            DeferredHolder.create(Registries.BLOCK, REGISTRATE.asResource("lava_fishing_net")));
    public static final BlockEntry<RoostBlock> ROOST = REGISTRATE
            .block("roost", RoostBlock::new)
            .properties(prop -> prop.strength(1.5F).sound(SoundType.BAMBOO_WOOD))
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
            .transform(axeOnly())
            .item(RoostBlockItem::new)
            .build()
            .register();
    public static final BlockEntry<ChickenRoostBlock> CHICKEN_ROOST = REGISTRATE
            .block("chicken_roost", prop -> new ChickenRoostBlock(prop, ROOST))
            .properties(prop -> prop.strength(1.5F).sound(SoundType.BAMBOO_WOOD))
            .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), AssetLookup.standardModel(ctx, prov)))
            .item()
            .build()
            .register();
    public static final BlockEntry<VacuumHarvesterBlock> VACUUM_HARVESTER = REGISTRATE
            .block("vacuum_harvester", VacuumHarvesterBlock::new)
            .lang("Vacuum Harvester")
            .initialProperties(SharedProperties::stone)
            .properties(properties -> properties.noOcclusion().mapColor(DyeColor.YELLOW))
            .transform(pickaxeOnly())
            .transform(CIFConfig.server().stressValues.setImpact(8.0))
            .blockstate((context, provider) -> provider.simpleBlock(context.get(), AssetLookup.partialBaseModel(context, provider)))
            .onRegister(MovementBehaviour.movementBehaviour(new VacuumHarvesterMovementBehaviour()))
            .item()
            .tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static void register(IEventBus modBus) {
        if (isLavaFishingNetEnabled())
            LavaFishingNetRegistration.register();
    }

    public static boolean isLavaFishingNetEnabled() {
        return ModIntegration.NETHER_DEPTHS_UPGRADE.enabled()
                || ModIntegration.TIDE.enabled()
                || ModIntegration.STARCATCHER.enabled()
                || ModIntegration.CONFLUENCE.enabled();
    }

    private static class LavaFishingNetRegistration {
        private static final BlockEntry<LavaFishingNetBlock> LAVA_FISHING_NET = REGISTRATE
                .block("lava_fishing_net", LavaFishingNetBlock::new)
                .lang("Lava Fishing Net")
                .initialProperties(SharedProperties::softMetal)
                .properties(properties -> {
                    var result = properties
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.CHAIN)
                            .noOcclusion();
                    return DatagenModLoader.isRunningDataGen() ? result.noLootTable() : result;
                })
                .asOptional()
                .transform(axeOnly())
                .tag(AllBlockTags.WINDMILL_SAILS.tag)
                .blockstate(BlockStateGen.directionalBlockProvider(false))
                .onRegister(block -> MovementBehaviour.REGISTRY.register(
                        block, new LavaFishingNetMovementBehaviour()))
                .simpleItem()
                .register();

        private static void register() {}
    }
}
