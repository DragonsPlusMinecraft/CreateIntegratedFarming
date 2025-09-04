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

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.ARM_TARGETS;
import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.CONTRAPTION_ACTOR;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

public class CIFPonderTags {
    public static final ResourceLocation FARMING_APPLIANCES = CIFCommon.asResource("farming_appliances");

    public static final ResourceLocation RANCHING_APPLIANCES = CIFCommon.asResource("ranching_appliances");

    public static final ResourceLocation FISHING_APPLIANCES = CIFCommon.asResource("fishing_appliances");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> entryHelper = helper.withKeyFunction(RegistryEntry::getId);

        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
                RegisteredObjectsHelper::getKeyOrThrow);

        helper.registerTag(FARMING_APPLIANCES)
                .addToIndex()
                .item(AllBlocks.MECHANICAL_HARVESTER, true, false)
                .title("Farming Appliances")
                .description("Components about farming")
                .register();

        helper.registerTag(RANCHING_APPLIANCES)
                .addToIndex()
                .item(CIFBlocks.ROOST, true, false)
                .title("Ranching Appliances")
                .description("Components about ranching")
                .register();

        helper.registerTag(FISHING_APPLIANCES)
                .addToIndex()
                .item(CIFBlocks.FISHING_NET, true, false)
                .title("Fishing Appliances")
                .description("Components about fishing")
                .register();

        entryHelper.addToTag(FARMING_APPLIANCES)
                .add(AllBlocks.MECHANICAL_HARVESTER)
                .add(AllBlocks.SPOUT);

        entryHelper.addToTag(RANCHING_APPLIANCES)
                .add(CIFBlocks.ROOST)
                .add(CIFBlocks.CHICKEN_ROOST);

        entryHelper.addToTag(FISHING_APPLIANCES)
                .add(CIFBlocks.FISHING_NET)
                .add(AllBlocks.DEPLOYER);

        entryHelper.addToTag(ARM_TARGETS)
                .add(CIFBlocks.CHICKEN_ROOST);
    }
}
