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

package plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenRoostBlock;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlockEntities;

public class VanillaBackportChickenRoostBlock extends ChickenRoostBlock {
    public static final String VARIANT_TAG = "variant";
    public static final ResourceLocation WARM_VARIANT = ResourceLocation.withDefaultNamespace("warm");
    public static final ResourceLocation COLD_VARIANT = ResourceLocation.withDefaultNamespace("cold");

    private final ResourceLocation variant;

    public VanillaBackportChickenRoostBlock(
            Properties properties, Holder<Block> empty, ResourceLocation variant) {
        super(properties, empty);
        this.variant = variant;
    }

    @Override
    protected Chicken createChicken(Level level) {
        Chicken chicken = super.createChicken(level);
        CompoundTag tag = new CompoundTag();
        chicken.saveWithoutId(tag);
        tag.putString(VARIANT_TAG, variant.toString());
        chicken.load(tag);
        return chicken;
    }

    @Override
    protected MapCodec<? extends VanillaBackportChickenRoostBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                BuiltInRegistries.BLOCK.holderByNameCodec()
                        .fieldOf("empty")
                        .forGetter(block -> block.empty),
                ResourceLocation.CODEC.fieldOf("variant").forGetter(block -> block.variant))
                .apply(instance, VanillaBackportChickenRoostBlock::new));
    }

    @Override
    public BlockEntityType<? extends VanillaBackportChickenRoostBlockEntity> getBlockEntityType() {
        return VanillaBackportBlockEntities.CHICKEN_ROOST.get();
    }
}
