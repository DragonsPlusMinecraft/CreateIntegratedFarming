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

package plus.dragons.createintegratedfarming.api.harvester;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Context shared by stationary and contraption-mounted area harvesters.
 */
public final class AreaHarvestContext {
    private final Level level;
    private final boolean replant;
    private final boolean harvestPartiallyGrown;
    private final ItemStack tool;
    private final Consumer<ItemStack> collector;
    private final SeedExtractor seedExtractor;

    public AreaHarvestContext(
            Level level,
            boolean replant,
            boolean harvestPartiallyGrown,
            ItemStack tool,
            Consumer<ItemStack> collector,
            SeedExtractor seedExtractor) {
        this.level = Objects.requireNonNull(level);
        this.replant = replant;
        this.harvestPartiallyGrown = harvestPartiallyGrown;
        this.tool = tool.copy();
        this.collector = Objects.requireNonNull(collector);
        this.seedExtractor = Objects.requireNonNull(seedExtractor);
    }

    public Level level() {
        return level;
    }

    public boolean replant() {
        return replant;
    }

    public boolean harvestPartiallyGrown() {
        return harvestPartiallyGrown;
    }

    public ItemStack tool() {
        return tool.copy();
    }

    public ItemStack tool(ItemStack fallback) {
        return tool.isEmpty() ? fallback.copy() : tool();
    }

    public void collect(ItemStack stack) {
        if (!stack.isEmpty())
            collector.accept(stack);
    }

    public ItemStack extractSeed(Predicate<ItemStack> predicate, int amount) {
        return seedExtractor.extract(predicate, amount);
    }

    @FunctionalInterface
    public interface SeedExtractor {
        ItemStack extract(Predicate<ItemStack> predicate, int amount);
    }
}
