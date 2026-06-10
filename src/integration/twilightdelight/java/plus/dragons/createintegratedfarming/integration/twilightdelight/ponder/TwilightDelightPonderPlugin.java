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

package plus.dragons.createintegratedfarming.integration.twilightdelight.ponder;

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.ARM_TARGETS;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderTags;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class TwilightDelightPonderPlugin {
    public static void register() {
        CIFPonderPlugin.SCENES.add(TwilightDelightPonderPlugin::registerScenes);
        CIFPonderPlugin.TAGS.add(TwilightDelightPonderPlugin::registerTags);
    }

    private static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(ModIntegration.TWILIGHT_FOREST.asResource("huge_water_lily"))
                .addStoryBoard("twilightdelight/lily_pad_arm", TwilightDelightPonderScenes::harvestLilyPads, CIFPonderTags.FARMING_APPLIANCES, ARM_TARGETS);
    }

    private static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(ARM_TARGETS)
                .add(ModIntegration.TWILIGHT_FOREST.asResource("huge_water_lily"));
        helper.addToTag(CIFPonderTags.FARMING_APPLIANCES)
                .add(ModIntegration.TWILIGHT_FOREST.asResource("huge_water_lily"));
    }
}
