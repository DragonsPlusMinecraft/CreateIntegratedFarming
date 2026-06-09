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

package plus.dragons.createintegratedfarming.integration.twilightdelight.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class LilyPadHarvestBehaviour implements CustomHarvestBehaviour {
    private static final ResourceLocation LILY_PAD = ResourceLocation.withDefaultNamespace("lily_pad");
    private static final ResourceLocation HUGE_LILY_PAD = ModIntegration.TWILIGHT_FOREST.asResource("huge_lily_pad");
    private static final ResourceLocation HUGE_WATER_LILY = ModIntegration.TWILIGHT_FOREST.asResource("huge_water_lily");
    private static final Map<ResourceLocation, LilyPadType> LILY_PAD_TYPES = Map.of(
            LILY_PAD, LilyPadType.LILY_PAD,
            HUGE_LILY_PAD, LilyPadType.HUGE_LILY_PAD,
            HUGE_WATER_LILY, LilyPadType.HUGE_WATER_LILY);

    private final LilyPadType type;

    protected LilyPadHarvestBehaviour(LilyPadType type) {
        this.type = type;
    }

    public static @Nullable LilyPadHarvestBehaviour create(Block block) {
        var type = LILY_PAD_TYPES.get(BuiltInRegistries.BLOCK.getKey(block));
        return type == null ? null : new LilyPadHarvestBehaviour(type);
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        if (type.preserveWhenReplanting && CustomHarvestBehaviour.replant())
            return;
        BlockHelper.destroyBlockAs(
                context.world,
                pos,
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    private enum LilyPadType {
        LILY_PAD(false),
        HUGE_LILY_PAD(true),
        HUGE_WATER_LILY(true);

        private final boolean preserveWhenReplanting;

        LilyPadType(boolean preserveWhenReplanting) {
            this.preserveWhenReplanting = preserveWhenReplanting;
        }
    }
}
