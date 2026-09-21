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
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createdragonsplus.client.ponder.PonderTagGroups;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

public class CIFPonderTags {
    public static final ResourceLocation FARMING_APPLIANCES = CIFCommon.asResource("farming_appliances");
    public static final ResourceLocation RANCHING_APPLIANCES = FARMING_APPLIANCES;
    public static final ResourceLocation FISHING_APPLIANCES = FARMING_APPLIANCES;
    public static final ResourceLocation OCCUPIED_ROOSTS = CIFCommon.asResource("occupied_roosts");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> entryHelper = helper.withKeyFunction(RegistryEntry::getId);
        helper.registerTag(FARMING_APPLIANCES)
                .addToIndex()
                .item(AllBlocks.MECHANICAL_HARVESTER, true, false)
                .title("Farming Appliances")
                .description("Components for crop farming, animal husbandry and fishing")
                .register();

        entryHelper.addToTag(FARMING_APPLIANCES)
                .add(AllBlocks.MECHANICAL_HARVESTER)
                .add(CIFBlocks.VACUUM_HARVESTER)
                .add(AllBlocks.SPOUT)
                .add(CIFBlocks.ROOST)
                .add(CIFBlocks.FISHING_NET)
                .add(AllBlocks.DEPLOYER);
        entryHelper.addToTag(CONTRAPTION_ACTOR).add(CIFBlocks.VACUUM_HARVESTER);
        if (CIFBlocks.isLavaFishingNetEnabled()) {
            entryHelper.addToTag(FARMING_APPLIANCES).add(CIFBlocks.LAVA_FISHING_NET);
            entryHelper.addToTag(CONTRAPTION_ACTOR).add(CIFBlocks.LAVA_FISHING_NET);
            entryHelper.addToTag(ARM_TARGETS).add(CIFBlocks.LAVA_FISHING_NET);
        }
        Component title = Component.translatable("create_integrated_farming.ponder.group.occupied_roosts");
        PonderTagGroups.registerGroup(FARMING_APPLIANCES, OCCUPIED_ROOSTS, title);
        PonderTagGroups.registerGroup(ARM_TARGETS, OCCUPIED_ROOSTS, title);
        addRoosts(helper, CIFBlocks.CHICKEN_ROOST.getId());
    }

    public static void addRoosts(PonderTagRegistrationHelper<ResourceLocation> helper, ResourceLocation... roosts) {
        PonderTagGroups.addToGroup(helper, FARMING_APPLIANCES, OCCUPIED_ROOSTS).add(roosts);
        PonderTagGroups.addToGroup(helper, ARM_TARGETS, OCCUPIED_ROOSTS).add(roosts);
    }
}
