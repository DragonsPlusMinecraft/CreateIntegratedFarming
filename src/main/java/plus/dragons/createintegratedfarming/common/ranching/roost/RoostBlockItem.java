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
        var capturable = RoostCapturable.resolve(target);
        return capturable == null ? InteractionResult.PASS : capturable.captureItem(target.level(), stack, hand, player, target);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return super.useOn(context);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SpawnerBlockEntity))
            return super.useOn(context);
        BaseSpawner spawner = ((SpawnerBlockEntity) blockEntity).getSpawner();
        List<SpawnData> possibleSpawns = spawner.spawnPotentials.unwrap()
                .stream()
                .map(WeightedEntry.Wrapper::data)
                .toList();
        if (possibleSpawns.isEmpty()) {
            possibleSpawns = new ArrayList<>();
            possibleSpawns.add(spawner.nextSpawnData);
        }
        for (SpawnData spawnData : possibleSpawns) {
            Optional<EntityType<?>> optional = EntityType.by(spawnData.entityToSpawn());
            if (optional.isEmpty())
                continue;
            var entity = optional.get().create(level);
            if (entity == null)
                continue;
            entity.load(spawnData.entityToSpawn());
            var capturable = RoostCapturable.resolve(entity);
            if (capturable == null)
                continue;
            return capturable.captureItem(level, context.getItemInHand(), context.getHand(), player, entity);
        }
        return super.useOn(context);
    }
}
