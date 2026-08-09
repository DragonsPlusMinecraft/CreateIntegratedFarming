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

package plus.dragons.createintegratedfarming.integration.environmental.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderTags;
import plus.dragons.createintegratedfarming.client.ponder.scene.RoostScene;
import plus.dragons.createintegratedfarming.integration.environmental.registry.EnvironmentalBlocks;

public class EnvironmentalPonderPlugin {
    public static void register() {
        CIFPonderPlugin.TAGS.add(EnvironmentalPonderPlugin::registerTags);
        CIFPonderPlugin.SCENES.add(EnvironmentalPonderPlugin::registerScenes);
    }

    private static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(CIFPonderTags.RANCHING_APPLIANCES).add(EnvironmentalBlocks.DUCK_ROOST.getId());
        helper.addToTag(AllCreatePonderTags.ARM_TARGETS).add(EnvironmentalBlocks.DUCK_ROOST.getId());
    }

    private static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(EnvironmentalBlocks.DUCK_ROOST.getId())
                .addStoryBoard(
                        "roost/operate", RoostScene::operate,
                        CIFPonderTags.RANCHING_APPLIANCES, AllCreatePonderTags.ARM_TARGETS);
    }
}
