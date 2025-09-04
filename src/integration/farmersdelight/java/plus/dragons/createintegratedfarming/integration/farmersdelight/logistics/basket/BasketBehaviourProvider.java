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

package plus.dragons.createintegratedfarming.integration.farmersdelight.logistics.basket;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.Direction;
import plus.dragons.createdragonsplus.common.behaviours.SmartBlockEntityBehaviourProvider;
import vectorwing.farmersdelight.common.block.BasketBlock;
import vectorwing.farmersdelight.common.block.entity.BasketBlockEntity;

public class BasketBehaviourProvider extends SmartBlockEntityBehaviourProvider<BasketBlockEntity> {
    public BasketBehaviourProvider(BasketBlockEntity basket) {
        super(basket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
                .allowingBeltFunnelsWhen(this::allowsBeltFunnels)
                .onlyInsertWhen(this::canInsert));
    }

    protected boolean allowsBeltFunnels() {
        return getBlockState().getValue(BasketBlock.FACING) == Direction.UP;
    }

    protected boolean canInsert(Direction side) {
        Direction facing = getBlockState().getValue(BasketBlock.FACING);
        return switch (facing) {
            case UP -> true;
            case DOWN -> false;
            default -> facing == side.getOpposite();
        };
    }
}
