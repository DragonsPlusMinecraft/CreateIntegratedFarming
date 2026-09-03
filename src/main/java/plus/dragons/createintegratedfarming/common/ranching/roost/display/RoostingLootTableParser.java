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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.IntRange;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.LootDisplayStatus;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.OutputDisplay;

/** Conservatively extracts only statically provable item outputs from a loaded loot table. */
@ApiStatus.Internal
public final class RoostingLootTableParser {
    private final MinecraftServer server;

    public RoostingLootTableParser(MinecraftServer server) {
        this.server = server;
    }

    public Result parse(ResourceKey<LootTable> key) {
        return parse(key, new HashSet<>());
    }

    private Result parse(ResourceKey<LootTable> key, Set<ResourceKey<LootTable>> visiting) {
        var registries = server.reloadableRegistries().get();
        LootTable table = registries.registryOrThrow(Registries.LOOT_TABLE).get(key);
        if (table == null)
            return Result.missing();
        if (!visiting.add(key))
            return Result.complex();
        try {
            JsonElement encoded = LootTable.DIRECT_CODEC
                    .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), table)
                    .getOrThrow(error -> new IllegalStateException("Could not encode loot table " + key.location() + ": " + error));
            return parseTable(encoded.getAsJsonObject(), visiting);
        } catch (RuntimeException exception) {
            CIFCommon.LOGGER.warn("Could not inspect roost production loot table {}", key.location(), exception);
            return Result.complex();
        } finally {
            visiting.remove(key);
        }
    }

    private Result parseTable(JsonObject table, Set<ResourceKey<LootTable>> visiting) {
        Result result = Result.exact();
        JsonArray pools = array(table, "pools");
        JsonArray functions = array(table, "functions");
        for (JsonElement element : pools)
            result = result.combine(parsePool(element.getAsJsonObject(), visiting, functions));
        if (pools.size() > 1)
            result = result.withStatus(LootDisplayStatus.COMPLEX);
        return result.normalized();
    }

    private Result parsePool(
            JsonObject pool, Set<ResourceKey<LootTable>> visiting, JsonArray tableFunctions) {
        Optional<IntRange> rolls = numberRange(pool.get("rolls"), 1);
        if (rolls.isEmpty() || !rolls.get().isExact())
            return Result.complex();
        int rollCount = rolls.get().minimum();
        if (rollCount < 0)
            return Result.complex();

        JsonArray entries = array(pool, "entries");
        Result result = Result.exact();
        boolean choice = entries.size() > 1 || rollCount == 0 || hasConditions(pool);
        for (JsonElement element : entries)
            result = result.combine(parseEntry(element.getAsJsonObject(), visiting, choice));
        result = applyFunctions(result, array(pool, "functions"));
        result = applyFunctions(result, tableFunctions);
        result = result.multiply(rollCount);

        Optional<IntRange> bonusRolls = numberRange(pool.get("bonus_rolls"), 0);
        if (bonusRolls.isEmpty() || bonusRolls.get().minimum() != 0 || bonusRolls.get().maximum() != 0)
            result = result.withStatus(LootDisplayStatus.COMPLEX);
        return result;
    }

    private Result parseEntry(JsonObject entry, Set<ResourceKey<LootTable>> visiting, boolean parentConditional) {
        String type = string(entry, "type");
        boolean conditional = parentConditional || hasConditions(entry);
        Result result;
        switch (type) {
            case "minecraft:item" -> result = parseItem(entry);
            case "minecraft:tag" -> result = parseTag(entry);
            case "minecraft:empty" -> result = Result.exact();
            case "minecraft:alternatives", "minecraft:group", "minecraft:sequence" -> {
                result = Result.exact();
                for (JsonElement child : array(entry, "children"))
                    result = result.combine(parseEntry(child.getAsJsonObject(), visiting, true));
                conditional = true;
            }
            case "minecraft:loot_table" -> result = parseNested(entry.get("value"), visiting);
            default -> result = Result.complex();
        }
        result = applyFunctions(result, array(entry, "functions"));
        return conditional ? result.conditional() : result;
    }

    private Result parseItem(JsonObject entry) {
        ResourceLocation id = ResourceLocation.tryParse(string(entry, "name"));
        if (id == null)
            return Result.complex();
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == null || !BuiltInRegistries.ITEM.containsKey(id))
            return Result.complex();
        return Result.output(new ItemStack(item));
    }

    private Result parseTag(JsonObject entry) {
        ResourceLocation id = ResourceLocation.tryParse(string(entry, "name"));
        if (id == null)
            return Result.complex();
        var tag = TagKey.create(Registries.ITEM, id);
        Result result = Result.exact();
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag))
            result = result.combine(Result.output(new ItemStack(holder.value())).conditional());
        return result;
    }

    private Result parseNested(JsonElement value, Set<ResourceKey<LootTable>> visiting) {
        if (value == null)
            return Result.complex();
        if (value.isJsonPrimitive()) {
            ResourceLocation id = ResourceLocation.tryParse(value.getAsString());
            return id == null
                    ? Result.complex()
                    : parse(ResourceKey.create(Registries.LOOT_TABLE, id), visiting);
        }
        return value.isJsonObject() ? parseTable(value.getAsJsonObject(), visiting) : Result.complex();
    }

    private Result applyFunctions(Result result, JsonArray functions) {
        for (JsonElement element : functions) {
            JsonObject function = element.getAsJsonObject();
            if (!"minecraft:set_count".equals(string(function, "function")))
                return Result.complex();
            Optional<IntRange> count = numberRange(function.get("count"));
            if (count.isEmpty())
                return Result.complex();
            boolean add = function.has("add") && function.get("add").getAsBoolean();
            Result original = result;
            result = result.setCount(count.get(), add);
            if (hasConditions(function))
                result = result.unionCounts(original).conditional();
        }
        return result;
    }

    private static Optional<IntRange> numberRange(JsonElement element) {
        if (element == null)
            return Optional.empty();
        if (element.isJsonPrimitive())
            return integer(element.getAsJsonPrimitive()).map(IntRange::exact);
        if (!element.isJsonObject())
            return Optional.empty();
        JsonObject object = element.getAsJsonObject();
        String type = string(object, "type");
        if (type.isEmpty() || "minecraft:constant".equals(type))
            return numberRange(object.get("value"));
        if ("minecraft:uniform".equals(type)) {
            Optional<IntRange> minimum = numberRange(object.get("min"));
            Optional<IntRange> maximum = numberRange(object.get("max"));
            if (minimum.isEmpty() || maximum.isEmpty())
                return Optional.empty();
            int lower = minimum.get().minimum();
            int upper = maximum.get().maximum();
            return upper < lower ? Optional.empty() : Optional.of(new IntRange(lower, upper));
        }
        return Optional.empty();
    }

    private static Optional<IntRange> numberRange(JsonElement element, int defaultValue) {
        return element == null ? Optional.of(IntRange.exact(defaultValue)) : numberRange(element);
    }

    private static Optional<Integer> integer(JsonPrimitive primitive) {
        if (!primitive.isNumber())
            return Optional.empty();
        double value = primitive.getAsDouble();
        if (!Double.isFinite(value) || value != Math.rint(value) || value < 0 || value > Integer.MAX_VALUE)
            return Optional.empty();
        return Optional.of((int) value);
    }

    private static boolean hasConditions(JsonObject object) {
        return object.has("conditions") && !array(object, "conditions").isEmpty();
    }

    private static JsonArray array(JsonObject object, String name) {
        JsonElement element = object.get(name);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
    }

    private static String string(JsonObject object, String name) {
        JsonElement element = object.get(name);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
    }

    public record Result(List<OutputDisplay> outputs, LootDisplayStatus status) {
        public Result {
            outputs = List.copyOf(outputs);
        }

        private static Result exact() {
            return new Result(List.of(), LootDisplayStatus.EXACT);
        }

        private static Result output(ItemStack stack) {
            return new Result(List.of(new OutputDisplay(stack, IntRange.exact(1), false)), LootDisplayStatus.EXACT);
        }

        private static Result complex() {
            return new Result(List.of(), LootDisplayStatus.COMPLEX);
        }

        private static Result missing() {
            return new Result(List.of(), LootDisplayStatus.MISSING);
        }

        private Result combine(Result other) {
            var combined = new ArrayList<OutputDisplay>(outputs);
            combined.addAll(other.outputs);
            return new Result(combined, status.merge(other.status));
        }

        private Result conditional() {
            return new Result(
                    outputs.stream()
                            .map(output -> new OutputDisplay(output.ingredient(), output.count(), true))
                            .toList(),
                    status == LootDisplayStatus.EXACT ? LootDisplayStatus.CONDITIONAL : status);
        }

        private Result multiply(int multiplier) {
            if (multiplier == 1)
                return this;
            return new Result(
                    outputs.stream()
                            .map(output -> new OutputDisplay(
                                    output.ingredient(), multiply(output, multiplier), output.conditional()))
                            .toList(),
                    status);
        }

        private static IntRange multiply(OutputDisplay output, int multiplier) {
            if (multiplier == 0)
                return IntRange.exact(0);
            if (output.conditional())
                return new IntRange(
                        output.count().minimum(),
                        Math.multiplyExact(output.count().maximum(), multiplier));
            return output.count().multiply(multiplier);
        }

        private Result setCount(IntRange count, boolean add) {
            return new Result(
                    outputs.stream()
                            .map(output -> new OutputDisplay(
                                    output.ingredient(), add ? add(output.count(), count) : count, output.conditional()))
                            .toList(),
                    status);
        }

        private Result withStatus(LootDisplayStatus newStatus) {
            return new Result(outputs, status.merge(newStatus));
        }

        private Result normalized() {
            Map<Item, OutputDisplay> unique = new LinkedHashMap<>();
            outputs.stream()
                    .filter(output -> output.count().maximum() > 0)
                    .sorted((left, right) -> BuiltInRegistries.ITEM
                            .getKey(left.ingredient().getItem())
                            .compareTo(BuiltInRegistries.ITEM.getKey(right.ingredient().getItem())))
                    .forEach(output -> unique.merge(output.ingredient().getItem(), output, Result::mergeOutput));
            return new Result(List.copyOf(unique.values()), status);
        }

        private Result unionCounts(Result other) {
            Map<Item, OutputDisplay> combined = new LinkedHashMap<>();
            outputs.forEach(output -> combined.put(output.ingredient().getItem(), output));
            other.outputs.forEach(output -> combined.merge(
                    output.ingredient().getItem(), output, Result::mergeOutput));
            return new Result(List.copyOf(combined.values()), status.merge(other.status));
        }

        private static OutputDisplay mergeOutput(OutputDisplay left, OutputDisplay right) {
            return new OutputDisplay(
                    left.ingredient(),
                    new IntRange(
                            Math.min(left.count().minimum(), right.count().minimum()),
                            Math.max(left.count().maximum(), right.count().maximum())),
                    left.conditional() || right.conditional());
        }

        private static IntRange add(IntRange left, IntRange right) {
            return new IntRange(
                    Math.addExact(left.minimum(), right.minimum()),
                    Math.addExact(left.maximum(), right.maximum()));
        }
    }
}
