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

package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockEntities;

public class VacuumHarvesterBlock extends KineticBlock implements IBE<VacuumHarvesterBlockEntity> {
    public static final MapCodec<VacuumHarvesterBlock> CODEC = simpleCodec(VacuumHarvesterBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 2, 2, 2, 14),
            box(1, 0, 1, 15, 2, 2),
            box(14, 0, 2, 15, 2, 14),
            box(1, 0, 14, 15, 2, 15),
            box(2, 1, 2, 14, 7, 14),
            box(3, 7, 3, 4, 14, 13),
            box(12, 7, 3, 13, 14, 13),
            box(4, 7, 3, 12, 14, 4),
            box(4, 7, 12, 12, 14, 13),
            box(5, 7, 5, 11, 14, 11),
            box(2, 14, 2, 14, 16, 14))
            .optimize();

    public VacuumHarvesterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, harvester -> harvester.giveContentsTo(player) ? InteractionResult.SUCCESS : InteractionResult.PASS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return ItemHelper.calcRedstoneFromBlockEntity(this, level, pos);
    }

    @Override
    public Class<VacuumHarvesterBlockEntity> getBlockEntityClass() {
        return VacuumHarvesterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends VacuumHarvesterBlockEntity> getBlockEntityType() {
        return CIFBlockEntities.VACUUM_HARVESTER.get();
    }

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }
}
