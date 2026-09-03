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

package plus.dragons.createintegratedfarming.client.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.FluidFeedDisplay;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.IntRange;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.ItemFeedDisplay;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplayRecipe.OutputDisplay;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

public final class RoostingCategory implements IRecipeCategory<RoostingDisplayRecipe> {
    private static final int WIDTH = 177;
    private static final int HEIGHT = 70;
    private static final String ITEM_FEED_SLOT = "item_feed";
    private static final String FLUID_FEED_SLOT = "fluid_feed";
    private static final IDrawable SLOT = asDrawable(AllGuiTextures.JEI_SLOT);

    private final IDrawable background;
    private final IDrawable icon;

    public RoostingCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableItemLike(CIFBlocks.ROOST.get());
    }

    @Override
    public RecipeType<RoostingDisplayRecipe> getRecipeType() {
        return CIFJeiPlugin.ROOSTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("create_integrated_farming.jei.roosting");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RoostingDisplayRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 23)
                .setSlotName(ITEM_FEED_SLOT)
                .setBackground(SLOT, -1, -1)
                .addItemStacks(recipe.itemFeeds().stream()
                        .map(ItemFeedDisplay::ingredient)
                        .toList())
                .addRichTooltipCallback((view, tooltip) -> addItemFeedTooltip(recipe, view, tooltip));

        if (!recipe.fluidFeeds().isEmpty()) {
            long capacity = recipe.fluidFeeds().stream()
                    .mapToLong(feed -> feed.ingredient().getAmount())
                    .max()
                    .orElseThrow();
            builder.addSlot(RecipeIngredientRole.INPUT, 35, 23)
                    .setSlotName(FLUID_FEED_SLOT)
                    .setBackground(SLOT, -1, -1)
                    .addIngredients(
                            NeoForgeTypes.FLUID_STACK,
                            recipe.fluidFeeds().stream()
                                    .map(FluidFeedDisplay::ingredient)
                                    .toList())
                    .setFluidRenderer(capacity, false, 16, 16)
                    .addRichTooltipCallback((view, tooltip) -> addFluidFeedTooltip(recipe, view, tooltip));
        }

        if (!recipe.outputs().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 23)
                    .setBackground(SLOT, -1, -1)
                    .addItemStacks(recipe.outputs().stream()
                            .map(output -> displayedOutput(recipe, output))
                            .toList())
                    .addRichTooltipCallback((view, tooltip) -> addOutputTooltip(recipe, view, tooltip));
        }
    }

    @Override
    public void draw(
            RoostingDisplayRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics graphics,
            double mouseX,
            double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 67, 42);
        AllGuiTextures.JEI_ARROW.render(graphics, 106, 26);
        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable("create_integrated_farming.jei.roosting.optional_feeding"),
                7,
                9,
                AllGuiTextures.FONT_COLOR,
                false);
        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable(
                        "create_integrated_farming.jei.roosting.natural_time",
                        formatTicks(recipe.productionTime())),
                58,
                57,
                AllGuiTextures.FONT_COLOR,
                false);

        Block block = BuiltInRegistries.BLOCK.get(recipe.representativeBlock());
        BlockState blockState = block.defaultBlockState();
        if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
            blockState = blockState.setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
        }
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(80, 41, 100);
        pose.mulPose(Axis.XP.rotationDegrees(-15.5F));
        pose.mulPose(Axis.YP.rotationDegrees(22.5F));
        GuiGameElement.of(blockState)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .scale(20)
                .render(graphics);
        pose.popPose();

        if (recipe.outputs().isEmpty()) {
            AllGuiTextures.JEI_SLOT.render(graphics, 151, 22);
            AllGuiTextures.JEI_QUESTION_MARK.render(graphics, 154, 23);
        }
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            RoostingDisplayRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY) {
        if (inside(mouseX, mouseY, 58, 17, 43, 39)) {
            Block representative = BuiltInRegistries.BLOCK.get(recipe.representativeBlock());
            tooltip.add(representative.getName());
            if (!recipe.equivalentBlocks().isEmpty()) {
                tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.applies_to")
                        .withStyle(ChatFormatting.GRAY));
                for (ResourceLocation id : recipe.equivalentBlocks()) {
                    tooltip.add(Component.literal(" • ")
                            .append(BuiltInRegistries.BLOCK.get(id).getName())
                            .withStyle(ChatFormatting.GRAY));
                }
            }
            tooltip.add(Component.translatable(
                    "create_integrated_farming.jei.roosting.natural_cycle",
                    formatTicks(recipe.productionTime()))
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.feeding_optional")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.feeding_effect")
                    .withStyle(ChatFormatting.GRAY));
        } else if (recipe.outputs().isEmpty() && inside(mouseX, mouseY, 151, 22, 18, 18)) {
            switch (recipe.lootStatus()) {
                case MISSING -> tooltip.add(Component.translatable(
                        "create_integrated_farming.jei.roosting.missing_output")
                        .withStyle(ChatFormatting.GOLD));
                case COMPLEX -> {
                    tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.complex_output")
                            .withStyle(ChatFormatting.GOLD));
                    tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.loot_modifier_note")
                            .withStyle(ChatFormatting.GRAY));
                }
                default -> tooltip.add(Component.translatable(
                        "create_integrated_farming.jei.roosting.no_static_output")
                        .withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public ResourceLocation getRegistryName(RoostingDisplayRecipe recipe) {
        return recipe.id();
    }

    private static void addItemFeedTooltip(
            RoostingDisplayRecipe recipe, IRecipeSlotView view, ITooltipBuilder tooltip) {
        view.getDisplayedItemStack().flatMap(displayed -> recipe.itemFeeds().stream()
                .filter(feed -> ItemStack.isSameItemSameComponents(displayed, feed.ingredient()))
                .findFirst())
                .ifPresent(feed -> {
                    addFeedingTiming(tooltip, feed.progress(), feed.cooldown());
                    if (!feed.remainder().isEmpty()) {
                        tooltip.add(Component.translatable(
                                "create_integrated_farming.jei.roosting.returns",
                                feed.remainder().getHoverName())
                                .withStyle(ChatFormatting.GRAY));
                    }
                });
    }

    private static void addFluidFeedTooltip(
            RoostingDisplayRecipe recipe, IRecipeSlotView view, ITooltipBuilder tooltip) {
        view.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK)
                .flatMap(displayed -> recipe.fluidFeeds().stream()
                        .filter(feed -> FluidStack.isSameFluidSameComponents(displayed, feed.ingredient()))
                        .findFirst())
                .ifPresent(feed -> {
                    tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.spout")
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable(
                            "create_integrated_farming.jei.roosting.consumes",
                            feed.ingredient().getAmount())
                            .withStyle(ChatFormatting.GRAY));
                    addFeedingTiming(tooltip, feed.progress(), feed.cooldown());
                });
    }

    private static void addOutputTooltip(
            RoostingDisplayRecipe recipe, IRecipeSlotView view, ITooltipBuilder tooltip) {
        view.getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                .flatMap(displayed -> recipe.outputs().stream()
                        .filter(output -> ItemStack.isSameItemSameComponents(displayed, output.ingredient()))
                        .findFirst())
                .ifPresent(output -> {
                    if (recipe.lootStatus() != RoostingDisplayRecipe.LootDisplayStatus.COMPLEX
                            && (!output.count().isExact() || output.count().minimum() != 1)) {
                        tooltip.add(Component.translatable(
                                "create_integrated_farming.jei.roosting.output_count",
                                formatCount(output.count()))
                                .withStyle(ChatFormatting.GRAY));
                    }
                    if (output.conditional()) {
                        tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.conditional_output")
                                .withStyle(ChatFormatting.GOLD));
                    }
                    if (recipe.lootStatus() == RoostingDisplayRecipe.LootDisplayStatus.COMPLEX) {
                        tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.complex_output")
                                .withStyle(ChatFormatting.GOLD));
                        tooltip.add(Component.translatable("create_integrated_farming.jei.roosting.loot_modifier_note")
                                .withStyle(ChatFormatting.GRAY));
                    }
                });
    }

    private static void addFeedingTiming(ITooltipBuilder tooltip, IntRange progress, IntRange cooldown) {
        tooltip.add(Component.translatable(
                "create_integrated_farming.jei.roosting.progress",
                formatTicks(progress))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(
                "create_integrated_farming.jei.roosting.cooldown",
                formatTicks(cooldown))
                .withStyle(ChatFormatting.GRAY));
    }

    private static ItemStack displayedOutput(RoostingDisplayRecipe recipe, OutputDisplay output) {
        int count = recipe.lootStatus() != RoostingDisplayRecipe.LootDisplayStatus.COMPLEX
                && output.count().isExact()
                        ? Math.max(1, output.count().minimum())
                        : 1;
        return output.ingredient().copyWithCount(count);
    }

    private static String formatTicks(IntRange ticks) {
        String minimum = formatDuration(ticks.minimum());
        String maximum = formatDuration(ticks.maximum());
        return minimum.equals(maximum) ? minimum : minimum + "–" + maximum;
    }

    private static String formatDuration(int ticks) {
        int seconds = (ticks + 19) / 20;
        return "%d:%02d".formatted(seconds / 60, seconds % 60);
    }

    private static String formatCount(IntRange count) {
        return count.isExact()
                ? Integer.toString(count.minimum())
                : count.minimum() + "–" + count.maximum();
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static IDrawable asDrawable(AllGuiTextures texture) {
        return new IDrawable() {
            @Override
            public int getWidth() {
                return texture.getWidth();
            }

            @Override
            public int getHeight() {
                return texture.getHeight();
            }

            @Override
            public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
                texture.render(graphics, xOffset, yOffset);
            }
        };
    }
}
