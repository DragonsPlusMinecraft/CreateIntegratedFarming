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

package plus.dragons.createintegratedfarming.integration.environmental.ranching.roost;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.environmental.common.entity.animal.Duck;
import com.teamabnormals.environmental.core.registry.EnvironmentalEntityTypes;
import com.teamabnormals.environmental.core.registry.EnvironmentalSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createintegratedfarming.common.ranching.roost.BirdRoostBlock;
import plus.dragons.createintegratedfarming.integration.environmental.registry.EnvironmentalBlockEntities;

public class EnvironmentalDuckRoostBlock extends BirdRoostBlock<Duck, EnvironmentalDuckRoostBlockEntity> {
    public EnvironmentalDuckRoostBlock(Properties properties, Holder<Block> empty) {
        super(properties, empty);
    }

    @Override
    protected Class<Duck> getBirdClass() {
        return Duck.class;
    }

    @Override
    protected EntityType<Duck> getBirdType() {
        return EnvironmentalEntityTypes.DUCK.get();
    }

    @Override
    protected SoundEvent getEggSound() {
        return EnvironmentalSoundEvents.DUCK_EGG.get();
    }

    @Override
    protected SoundEvent getHurtSound() {
        return EnvironmentalSoundEvents.DUCK_HURT.get();
    }

    @Override
    protected boolean isBirdJockey(Duck bird) {
        return bird.isBirdJockey();
    }

    @Override
    protected MapCodec<? extends EnvironmentalDuckRoostBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("empty").forGetter(block -> block.empty))
                .apply(instance, EnvironmentalDuckRoostBlock::new));
    }

    @Override
    public Class<EnvironmentalDuckRoostBlockEntity> getBlockEntityClass() {
        return EnvironmentalDuckRoostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EnvironmentalDuckRoostBlockEntity> getBlockEntityType() {
        return EnvironmentalBlockEntities.DUCK_ROOST.get();
    }
}
