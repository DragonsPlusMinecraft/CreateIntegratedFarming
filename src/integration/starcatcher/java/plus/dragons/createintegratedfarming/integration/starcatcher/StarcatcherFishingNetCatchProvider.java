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

package plus.dragons.createintegratedfarming.integration.starcatcher;

import com.wdiscute.starcatcher.bobentity.FishingBobEntity;
import com.wdiscute.starcatcher.fish.CatchInfo.FishEntryType;
import com.wdiscute.starcatcher.fish.FishApi;
import com.wdiscute.starcatcher.fish.FishProperties;
import com.wdiscute.starcatcher.registry.SCDataAttachments;
import com.wdiscute.starcatcher.registry.SCItems;
import com.wdiscute.starcatcher.registry.fishrestrictions.AbstractFishRestriction.Context;
import com.wdiscute.starcatcher.registry.fishrestrictions.CaughtLimitRestriction;
import com.wdiscute.starcatcher.registry.fishrestrictions.RarityCountRestriction;
import com.wdiscute.starcatcher.registry.tackleskin.SCTackleSkins;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchContext;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProvider;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public class StarcatcherFishingNetCatchProvider implements FishingNetCatchProvider {
    private static final Set<String> INDEPENDENT_PROVIDER_NAMESPACES = Set.of(
            ModIntegration.TIDE.id(),
            ModIntegration.NETHER_DEPTHS_UPGRADE.id(),
            ModIntegration.CONFLUENCE.id());

    @Override
    public List<ItemStack> getCatch(FishingNetCatchContext context) {
        ItemStack rod = SCItems.ROD.get().getDefaultInstance();
        FishingBobEntity bob = new FishingBobEntity(
                context.level(), context.player(), rod, SCTackleSkins.BASE_TACKLE_SKIN.get());
        bob.modifiers.clear();
        bob.setPos(context.origin());
        try {
            FishProperties selected = select(context, bob, rod);
            if (selected == null)
                return List.of();
            ItemStack stack = FishApi.makeItemStackNonBucket(
                    selected, context.random().nextFloat() * 100.0F, false, context.player(), false);
            return stack.isEmpty() ? List.of() : List.of(stack);
        } finally {
            bob.discard();
            SCDataAttachments.remove(context.player(), SCDataAttachments.FISHING_BOB);
        }
    }

    private FishProperties select(FishingNetCatchContext context, FishingBobEntity bob, ItemStack rod) {
        for (FishProperties properties : FishApi.getNonFishes(context.level())) {
            if (properties.catchInfo().fishEntryType() == FishEntryType.EXTRA
                    || shouldSkip(context, properties))
                continue;
            if (properties.calculateChance(bob, context.level(), rod, Context.FISHING) > 0)
                return properties;
        }

        List<WeightedFish> available = new ArrayList<>();
        int totalWeight = 0;
        for (FishProperties properties : FishApi.getFishes(context.level())) {
            if (shouldSkip(context, properties))
                continue;
            int weight = properties.calculateChance(bob, context.level(), rod, Context.FISHING);
            if (weight <= 0)
                continue;
            available.add(new WeightedFish(properties, weight));
            totalWeight += weight;
        }
        if (totalWeight <= 0)
            return null;
        int target = context.random().nextInt(totalWeight);
        for (WeightedFish entry : available) {
            target -= entry.weight();
            if (target < 0)
                return entry.properties();
        }
        return null;
    }

    private boolean shouldSkip(FishingNetCatchContext context, FishProperties properties) {
        if (properties.restrictions().stream()
                .anyMatch(restriction -> restriction instanceof CaughtLimitRestriction
                        || restriction instanceof RarityCountRestriction))
            return true;
        ResourceLocation entryId = FishApi.getKey(context.level(), properties);
        return isProvidedIndependently(entryId.getNamespace());
    }

    private boolean isProvidedIndependently(String namespace) {
        return INDEPENDENT_PROVIDER_NAMESPACES.contains(namespace) && ModList.get().isLoaded(namespace);
    }

    private record WeightedFish(FishProperties properties, int weight) {}
}
