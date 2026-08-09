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

package plus.dragons.createintegratedfarming.common.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.*;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createintegratedfarming.common.CIFCommon;

public class CIFCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, CIFCommon.ID);
    public static final Holder<CreativeModeTab> BASE = TABS.register("base", CIFCreativeModeTabs::base);

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    private static CreativeModeTab base(ResourceLocation id) {
        return CreativeModeTab.builder()
                .title(REGISTRATE.addLang("itemGroup", id, "Create: Integrated Farming"))
                .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
                .icon(AllBlocks.MECHANICAL_HARVESTER::asStack)
                .displayItems(CIFCreativeModeTabs::buildBaseContents)
                .build();
    }

    private static void buildBaseContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(FISHING_NET);
        LAVA_FISHING_NET.asOptional().ifPresent(output::accept);
        output.accept(ROOST);
        output.accept(CHICKEN_ROOST);
        output.accept(VACUUM_HARVESTER);
    }
}
