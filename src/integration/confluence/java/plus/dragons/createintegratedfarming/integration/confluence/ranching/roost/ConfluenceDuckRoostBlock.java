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

package plus.dragons.createintegratedfarming.integration.confluence.ranching.roost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.confluence.terraentity.entity.animal.Duck;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.common.ranching.roost.BirdRoostBlock;
import plus.dragons.createintegratedfarming.integration.confluence.registry.ConfluenceBlockEntities;

public class ConfluenceDuckRoostBlock extends BirdRoostBlock<Duck, ConfluenceDuckRoostBlockEntity> {
    private final int variant;

    public ConfluenceDuckRoostBlock(Properties properties, Holder<Block> empty, int variant) {
        super(properties, empty);
        this.variant = variant;
    }

    @Override
    protected Class<Duck> getBirdClass() {
        return Duck.class;
    }

    @Override
    protected EntityType<Duck> getBirdType() {
        return TEAnimals.DUCK.get();
    }

    @Override
    protected @Nullable Duck createBird(Level level) {
        Duck duck = super.createBird(level);
        if (duck != null)
            duck.setVariant(variant);
        return duck;
    }

    @Override
    protected SoundEvent getEggSound() {
        return SoundEvents.CHICKEN_EGG;
    }

    @Override
    protected SoundEvent getHurtSound() {
        return TESounds.ROUTINE_HURT.get();
    }

    @Override
    protected boolean isBirdJockey(Duck bird) {
        return bird.isChickenJockey();
    }

    @Override
    protected MapCodec<? extends ConfluenceDuckRoostBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("empty").forGetter(block -> block.empty),
                Codec.intRange(Duck.MALLARD_ID, Duck.COMMON_ID)
                        .fieldOf("variant")
                        .forGetter(block -> block.variant))
                .apply(instance, ConfluenceDuckRoostBlock::new));
    }

    @Override
    public Class<ConfluenceDuckRoostBlockEntity> getBlockEntityClass() {
        return ConfluenceDuckRoostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ConfluenceDuckRoostBlockEntity> getBlockEntityType() {
        return ConfluenceBlockEntities.DUCK_ROOST.get();
    }
}
