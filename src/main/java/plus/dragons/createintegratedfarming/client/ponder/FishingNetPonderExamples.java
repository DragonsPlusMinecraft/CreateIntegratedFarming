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

package plus.dragons.createintegratedfarming.client.ponder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class FishingNetPonderExamples {
    private static final Map<ResourceLocation, FishingNetPonderExample> EXAMPLES = new LinkedHashMap<>();

    private FishingNetPonderExamples() {}

    public static synchronized void register(FishingNetPonderExample example) {
        if (EXAMPLES.putIfAbsent(example.id(), example) != null)
            throw new IllegalArgumentException("Duplicate fishing net Ponder example: " + example.id());
    }

    public static synchronized List<FishingNetPonderExample> shuffled() {
        List<FishingNetPonderExample> examples = new ArrayList<>(EXAMPLES.values());
        Collections.shuffle(examples);
        return examples;
    }
}
