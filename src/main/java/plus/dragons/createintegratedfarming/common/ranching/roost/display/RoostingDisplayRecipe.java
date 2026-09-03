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

package plus.dragons.createintegratedfarming.common.ranching.roost.display;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record RoostingDisplayRecipe(
        ResourceLocation id,
        ResourceLocation representativeBlock,
        List<ResourceLocation> equivalentBlocks,
        IntRange productionTime,
        List<ItemFeedDisplay> itemFeeds,
        List<FluidFeedDisplay> fluidFeeds,
        List<OutputDisplay> outputs,
        LootDisplayStatus lootStatus) {

    private static final int MAX_BLOCKS = 64;
    private static final int MAX_FEEDS = 4096;
    private static final int MAX_OUTPUTS = 4096;

    public static final StreamCodec<RegistryFriendlyByteBuf, RoostingDisplayRecipe> STREAM_CODEC = StreamCodec.ofMember(
            RoostingDisplayRecipe::encode,
            RoostingDisplayRecipe::decode);
    public RoostingDisplayRecipe {
        equivalentBlocks = List.copyOf(equivalentBlocks);
        itemFeeds = List.copyOf(itemFeeds);
        fluidFeeds = List.copyOf(fluidFeeds);
        outputs = List.copyOf(outputs);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        ResourceLocation.STREAM_CODEC.encode(buffer, id);
        ResourceLocation.STREAM_CODEC.encode(buffer, representativeBlock);
        writeList(buffer, equivalentBlocks, ResourceLocation.STREAM_CODEC);
        IntRange.STREAM_CODEC.encode(buffer, productionTime);
        writeList(buffer, itemFeeds, ItemFeedDisplay.STREAM_CODEC);
        writeList(buffer, fluidFeeds, FluidFeedDisplay.STREAM_CODEC);
        writeList(buffer, outputs, OutputDisplay.STREAM_CODEC);
        buffer.writeEnum(lootStatus);
    }

    private static RoostingDisplayRecipe decode(RegistryFriendlyByteBuf buffer) {
        return new RoostingDisplayRecipe(
                ResourceLocation.STREAM_CODEC.decode(buffer),
                ResourceLocation.STREAM_CODEC.decode(buffer),
                readList(buffer, ResourceLocation.STREAM_CODEC, MAX_BLOCKS),
                IntRange.STREAM_CODEC.decode(buffer),
                readList(buffer, ItemFeedDisplay.STREAM_CODEC, MAX_FEEDS),
                readList(buffer, FluidFeedDisplay.STREAM_CODEC, MAX_FEEDS),
                readList(buffer, OutputDisplay.STREAM_CODEC, MAX_OUTPUTS),
                buffer.readEnum(LootDisplayStatus.class));
    }

    private static <T> void writeList(
            RegistryFriendlyByteBuf buffer, List<T> values, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        buffer.writeVarInt(values.size());
        values.forEach(value -> codec.encode(buffer, value));
    }

    private static <T> List<T> readList(
            RegistryFriendlyByteBuf buffer,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            int maximumSize) {
        int size = buffer.readVarInt();
        if (size < 0 || size > maximumSize)
            throw new IllegalArgumentException("Invalid roosting display list size: " + size);
        var result = new ArrayList<T>(size);
        for (int i = 0; i < size; i++)
            result.add(codec.decode(buffer));
        return result;
    }

    public record IntRange(int minimum, int maximum) {
        public static final StreamCodec<RegistryFriendlyByteBuf, IntRange> STREAM_CODEC = StreamCodec.ofMember(
                IntRange::encode,
                IntRange::decode);

        public IntRange {
            if (minimum < 0 || maximum < minimum)
                throw new IllegalArgumentException("Invalid integer range: " + minimum + ".." + maximum);
        }

        public static IntRange exact(int value) {
            return new IntRange(value, value);
        }

        public boolean isExact() {
            return minimum == maximum;
        }

        public IntRange add(int value) {
            return new IntRange(Math.addExact(minimum, value), Math.addExact(maximum, value));
        }

        public IntRange multiply(int value) {
            return new IntRange(Math.multiplyExact(minimum, value), Math.multiplyExact(maximum, value));
        }

        private void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeVarInt(minimum);
            buffer.writeVarInt(maximum);
        }

        private static IntRange decode(RegistryFriendlyByteBuf buffer) {
            return new IntRange(buffer.readVarInt(), buffer.readVarInt());
        }
    }

    public record ItemFeedDisplay(
            ItemStack ingredient, IntRange progress, IntRange cooldown, ItemStack remainder) {

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemFeedDisplay> STREAM_CODEC = StreamCodec.ofMember(
                ItemFeedDisplay::encode,
                ItemFeedDisplay::decode);
        public ItemFeedDisplay {
            ingredient = ingredient.copy();
            remainder = remainder.copy();
        }

        private void encode(RegistryFriendlyByteBuf buffer) {
            ItemStack.STREAM_CODEC.encode(buffer, ingredient);
            IntRange.STREAM_CODEC.encode(buffer, progress);
            IntRange.STREAM_CODEC.encode(buffer, cooldown);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, remainder);
        }

        private static ItemFeedDisplay decode(RegistryFriendlyByteBuf buffer) {
            return new ItemFeedDisplay(
                    ItemStack.STREAM_CODEC.decode(buffer),
                    IntRange.STREAM_CODEC.decode(buffer),
                    IntRange.STREAM_CODEC.decode(buffer),
                    ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
        }
    }

    public record FluidFeedDisplay(FluidStack ingredient, IntRange progress, IntRange cooldown) {

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidFeedDisplay> STREAM_CODEC = StreamCodec.ofMember(
                FluidFeedDisplay::encode,
                FluidFeedDisplay::decode);
        public FluidFeedDisplay {
            ingredient = ingredient.copy();
        }

        private void encode(RegistryFriendlyByteBuf buffer) {
            FluidStack.STREAM_CODEC.encode(buffer, ingredient);
            IntRange.STREAM_CODEC.encode(buffer, progress);
            IntRange.STREAM_CODEC.encode(buffer, cooldown);
        }

        private static FluidFeedDisplay decode(RegistryFriendlyByteBuf buffer) {
            return new FluidFeedDisplay(
                    FluidStack.STREAM_CODEC.decode(buffer),
                    IntRange.STREAM_CODEC.decode(buffer),
                    IntRange.STREAM_CODEC.decode(buffer));
        }
    }

    public record OutputDisplay(ItemStack ingredient, IntRange count, boolean conditional) {

        public static final StreamCodec<RegistryFriendlyByteBuf, OutputDisplay> STREAM_CODEC = StreamCodec.ofMember(
                OutputDisplay::encode,
                OutputDisplay::decode);
        public OutputDisplay {
            ingredient = ingredient.copy();
        }

        private void encode(RegistryFriendlyByteBuf buffer) {
            ItemStack.STREAM_CODEC.encode(buffer, ingredient);
            IntRange.STREAM_CODEC.encode(buffer, count);
            buffer.writeBoolean(conditional);
        }

        private static OutputDisplay decode(RegistryFriendlyByteBuf buffer) {
            return new OutputDisplay(
                    ItemStack.STREAM_CODEC.decode(buffer),
                    IntRange.STREAM_CODEC.decode(buffer),
                    buffer.readBoolean());
        }
    }

    public enum LootDisplayStatus {
        EXACT,
        CONDITIONAL,
        COMPLEX,
        MISSING;

        public LootDisplayStatus merge(LootDisplayStatus other) {
            if (this == COMPLEX || other == COMPLEX)
                return COMPLEX;
            if (this == MISSING || other == MISSING)
                return MISSING;
            if (this == CONDITIONAL || other == CONDITIONAL)
                return CONDITIONAL;
            return EXACT;
        }
    }
}
