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

package plus.dragons.createintegratedfarming.common.ranching.roost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class RoostBlockItem extends BlockItem {
    public RoostBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        var capturable = RoostCapturable.REGISTRY.get(target.getType());
        return capturable == null ? InteractionResult.PASS : capturable.captureItem(target.level(), stack, hand, player, target);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = world.getBlockEntity(pos);
        Player player = context.getPlayer();
        if (!(be instanceof SpawnerBlockEntity))
            return super.useOn(context);
        BaseSpawner spawner = ((SpawnerBlockEntity) be).getSpawner();
        List<SpawnData> possibleSpawns = spawner.spawnPotentials.unwrap()
                .stream()
                .map(WeightedEntry.Wrapper::data)
                .toList();
        if (possibleSpawns.isEmpty()) {
            possibleSpawns = new ArrayList<>();
            possibleSpawns.add(spawner.nextSpawnData);
        }
        for (SpawnData e : possibleSpawns) {
            Optional<EntityType<?>> optionalEntity = EntityType.by(e.entityToSpawn());
            if (optionalEntity.isEmpty())
                continue;
            var capturable = RoostCapturable.REGISTRY.get(optionalEntity.get());
            if (capturable == null) continue;
            return capturable.captureItem(world, player.getItemInHand(context.getHand()), context.getHand(), player, optionalEntity.get().create(world));
        }
        return super.useOn(context);
    }
}
