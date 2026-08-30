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

package plus.dragons.createintegratedfarming.integration.confluence.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

public final class ConfluenceDuckModelLoader
        implements IGeometryLoader<ConfluenceDuckModelLoader.Geometry> {
    public static final ConfluenceDuckModelLoader INSTANCE = new ConfluenceDuckModelLoader();

    private ConfluenceDuckModelLoader() {}

    @Override
    public Geometry read(JsonObject json, JsonDeserializationContext context) {
        return Geometry.INSTANCE;
    }

    public static final class Geometry implements IUnbakedGeometry<Geometry> {
        private static final Geometry INSTANCE = new Geometry();

        private Geometry() {}

        @Override
        public BakedModel bake(
                IGeometryBakingContext context,
                ModelBaker baker,
                Function<Material, TextureAtlasSprite> spriteGetter,
                ModelState modelState,
                ItemOverrides overrides) {
            var particle = spriteGetter.apply(context.getMaterial("particle"));
            var duck = spriteGetter.apply(context.getMaterial("duck"));
            var renderTypeHint = context.getRenderTypeHint();
            var renderTypes = renderTypeHint == null
                    ? RenderTypeGroup.EMPTY
                    : context.getRenderType(renderTypeHint);
            var delegate = IModelBuilder.of(
                    context.useAmbientOcclusion(),
                    context.useBlockLight(),
                    context.isGui3d(),
                    context.getTransforms(),
                    overrides,
                    particle,
                    renderTypes)
                    .build();
            var rootTransform = context.getRootTransform();
            if (!rootTransform.isIdentity())
                modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
            return new BakedDuckModel(delegate, duck, modelState.getRotation());
        }
    }

    private static final class BakedDuckModel extends BakedModelWrapper<BakedModel> {
        private final TextureAtlasSprite sprite;
        private final Transformation modelTransform;
        private volatile QuadCache quadCache;

        private BakedDuckModel(
                BakedModel delegate, TextureAtlasSprite sprite, Transformation modelTransform) {
            super(delegate);
            this.sprite = sprite;
            this.modelTransform = modelTransform;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
            return side == null ? getDuckQuads() : List.of();
        }

        @Override
        public List<BakedQuad> getQuads(
                BlockState state,
                Direction side,
                RandomSource random,
                ModelData data,
                RenderType renderType) {
            return side == null ? getDuckQuads() : List.of();
        }

        private List<BakedQuad> getDuckQuads() {
            var mesh = ConfluenceDuckModelConverter.getMesh();
            if (mesh == null)
                return List.of();
            var cached = quadCache;
            if (cached != null && cached.mesh() == mesh)
                return cached.quads();
            synchronized (this) {
                cached = quadCache;
                if (cached == null || cached.mesh() != mesh) {
                    cached = new QuadCache(
                            mesh,
                            ConfluenceDuckModelConverter.bake(mesh, sprite, modelTransform));
                    quadCache = cached;
                }
                return cached.quads();
            }
        }
    }

    private record QuadCache(ConfluenceDuckModelConverter.Mesh mesh, List<BakedQuad> quads) {}
}
