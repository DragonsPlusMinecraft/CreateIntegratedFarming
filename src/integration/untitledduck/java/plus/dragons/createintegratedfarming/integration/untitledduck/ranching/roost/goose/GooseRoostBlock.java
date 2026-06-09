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

package plus.dragons.createintegratedfarming.integration.untitledduck.ranching.roost.goose;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.untitledduckmod.common.entity.GooseEntity;
import net.untitledduckmod.common.init.ModEntityTypes;
import net.untitledduckmod.common.init.ModSoundEvents;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlock;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturable;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlockEntities;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlocks;

public class GooseRoostBlock extends RoostBlock implements IBE<GooseRoostBlockEntity> {
    protected final Holder<Block> empty;
    protected final byte variant;

    public GooseRoostBlock(Properties properties, Holder<Block> empty, byte variant) {
        super(properties);
        this.empty = empty;
        this.variant = variant;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, coop -> {
            ItemStack stack = coop.outputHandler.extractItem(0, 64, false);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
                level.playSound(
                        player, pos, ModSoundEvents.GOOSE_LAY_EGG.get(), SoundSource.BLOCKS,
                        1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.LEAD)) {
            GooseEntity goose = gooseVariant(level);
            goose.setPos(pos.getCenter());
            goose.setLeashedTo(player, true);
            level.addFreshEntity(goose);
            level.setBlockAndUpdate(pos, empty.value().withPropertiesOf(state));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return onBlockEntityUseItemOn(level, pos, coop -> {
            if (coop != null && coop.feedItem(stack, false)) {
                if (!player.hasInfiniteMaterials())
                    stack.shrink(1);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    private GooseEntity gooseVariant(Level level) {
        GooseEntity goose = new GooseEntity(ModEntityTypes.GOOSE.get(), level);
        if (variant < 3) {
            goose.setVariant(variant);
        } else {
            switch (variant) {
                case 3:
                    goose.setCustomName(Component.literal("ping"));
                    break;
                case 4:
                    goose.setCustomName(Component.literal("sus"));
                    break;
                default:
                    goose.setCustomName(Component.literal("untitled"));
            }
        }
        return goose;
    }

    private static Block blockVariant(GooseEntity goose) {
        if (goose.getCustomName() != null) {
            if (goose.getCustomName().getString().equals("ping")) {
                return UntitledDuckBlocks.GOOSE_ROOST_PING.get();
            } else if (goose.getCustomName().getString().equals("sus")) {
                return UntitledDuckBlocks.GOOSE_ROOST_SUS.get();
            } else if (goose.getCustomName().getString().equals("untitled")) {
                return UntitledDuckBlocks.GOOSE_ROOST_UNTITLED.get();
            }
        }
        return switch (goose.getVariant()) {
            case 0b1 -> UntitledDuckBlocks.GOOSE_ROOST_CANADIAN.get();
            case 0b10 -> UntitledDuckBlocks.GOOSE_ROOST_GREYLAG.get();
            default -> UntitledDuckBlocks.GOOSE_ROOST_NORMAL.get();
        };
    }

    public static BlockState withVariantPropertiesOf(BlockState state, GooseEntity goose) {
        BlockState blockstate = blockVariant(goose).defaultBlockState();

        for (Property<?> property : state.getBlock().getStateDefinition().getProperties()) {
            if (blockstate.hasProperty(property)) {
                blockstate = copyProperty(state, blockstate, property);
            }
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState sourceState, BlockState targetState, Property<T> property) {
        return targetState.setValue(property, sourceState.getValue(property));
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
        if (!(entity instanceof ItemEntity itemEntity))
            return;
        if (!entity.isAlive())
            return;
        if (entity.level().isClientSide)
            return;

        DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get(level, entity.blockPosition(), DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour == null)
            return;
        ItemStack remainder = inputBehaviour.handleInsertion(itemEntity.getItem(), Direction.UP, false);
        itemEntity.setItem(remainder);
        if (remainder.isEmpty())
            itemEntity.discard();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
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
    protected MapCodec<? extends GooseRoostBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("empty").forGetter(block -> block.empty),
                Codec.BYTE.fieldOf("variant").forGetter(block -> block.variant))
                .apply(instance, GooseRoostBlock::new));
    }

    @Override
    public Class<GooseRoostBlockEntity> getBlockEntityClass() {
        return GooseRoostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GooseRoostBlockEntity> getBlockEntityType() {
        return UntitledDuckBlockEntities.GOOSE_ROOST.get();
    }

    public static class Capturable implements RoostCapturable {
        @Override
        public ItemInteractionResult captureBlock(Level level, BlockState state, BlockPos pos, ItemStack stack, Player player, Entity entity) {
            if (entity instanceof GooseEntity goose && !goose.isBaby()) {
                level.setBlockAndUpdate(pos, withVariantPropertiesOf(state, goose));
                goose.playSound(ModSoundEvents.GOOSE_HONK.get());
                goose.discard();
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public InteractionResult captureItem(Level level, ItemStack stack, InteractionHand hand, Player player, Entity entity) {
            if (entity instanceof GooseEntity goose && !goose.isBaby()) {
                if (player.hasInfiniteMaterials())
                    player.getInventory().placeItemBackInInventory(new ItemStack(blockVariant(goose).asItem()));
                else {
                    if (stack.getCount() == 1) player.setItemInHand(hand, new ItemStack(blockVariant(goose).asItem()));
                    else {
                        player.getInventory().placeItemBackInInventory(new ItemStack(blockVariant(goose).asItem()));
                        stack.shrink(1);
                    }
                }
                goose.playSound(ModSoundEvents.GOOSE_HONK.get());
                goose.discard();
                return InteractionResult.sidedSuccess(player.level().isClientSide);
            }
            return InteractionResult.PASS;
        }
    }
}
