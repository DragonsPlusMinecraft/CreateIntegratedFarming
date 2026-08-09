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

package plus.dragons.createintegratedfarming.integration.autumnity.ranching.roost;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.autumnity.common.entity.animal.Turkey;
import com.teamabnormals.autumnity.core.registry.AutumnityEntityTypes;
import com.teamabnormals.autumnity.core.registry.AutumnitySoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createintegratedfarming.common.ranching.roost.BirdRoostBlock;
import plus.dragons.createintegratedfarming.integration.autumnity.registry.AutumnityBlockEntities;

public class AutumnityTurkeyRoostBlock extends BirdRoostBlock<Turkey, AutumnityTurkeyRoostBlockEntity> {
    public AutumnityTurkeyRoostBlock(Properties properties, Holder<Block> empty) {
        super(properties, empty);
    }

    @Override
    protected Class<Turkey> getBirdClass() {
        return Turkey.class;
    }

    @Override
    protected EntityType<Turkey> getBirdType() {
        return AutumnityEntityTypes.TURKEY.get();
    }

    @Override
    protected SoundEvent getEggSound() {
        return AutumnitySoundEvents.ENTITY_TURKEY_EGG.get();
    }

    @Override
    protected SoundEvent getHurtSound() {
        return AutumnitySoundEvents.ENTITY_TURKEY_HURT.get();
    }

    @Override
    protected boolean isBirdJockey(Turkey bird) {
        return bird.isBirdJockey();
    }

    @Override
    protected MapCodec<? extends AutumnityTurkeyRoostBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("empty").forGetter(block -> block.empty))
                .apply(instance, AutumnityTurkeyRoostBlock::new));
    }

    @Override
    public Class<AutumnityTurkeyRoostBlockEntity> getBlockEntityClass() {
        return AutumnityTurkeyRoostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AutumnityTurkeyRoostBlockEntity> getBlockEntityType() {
        return AutumnityBlockEntities.TURKEY_ROOST.get();
    }
}
