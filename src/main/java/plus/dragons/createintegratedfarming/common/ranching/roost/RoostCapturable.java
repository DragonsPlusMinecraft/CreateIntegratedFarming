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

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface RoostCapturable {
    SimpleRegistry<EntityType<?>, RoostCapturable> REGISTRY = SimpleRegistry.create();

    static @Nullable RoostCapturable resolve(Entity entity) {
        RoostCapturableProvider provider = RoostCapturableProvider.REGISTRY.get(entity.getType());
        if (provider != null) {
            RoostCapturable capturable = provider.getCapturable(entity);
            if (capturable != null)
                return capturable;
        }
        return REGISTRY.get(entity.getType());
    }

    ItemInteractionResult captureBlock(Level level, BlockState state, BlockPos pos, ItemStack stack, Player player, Entity entity);

    InteractionResult captureItem(Level level, ItemStack stack, InteractionHand hand, Player player, Entity entity);
}
