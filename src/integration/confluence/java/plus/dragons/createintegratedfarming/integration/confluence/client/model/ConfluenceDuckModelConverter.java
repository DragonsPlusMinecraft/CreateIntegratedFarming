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

import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeStack;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.util.RenderUtil;

final class ConfluenceDuckModelConverter {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            "terra_entity", "geo/entity/animal/duck.geo.json");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            "terra_entity", "animations/entity/animal/duck.animation.json");
    private static final String IDLE_ANIMATION = "misc.idle";
    private static final float GROUND_HEIGHT = 4.0F / 16.0F;
    private static final Object CACHE_LOCK = new Object();
    private static final AtomicBoolean MISSING_CACHE_WARNING = new AtomicBoolean();
    private static volatile MeshCache meshCache;
    private static volatile FailedCache failedCache;

    private ConfluenceDuckModelConverter() {}

    static Mesh getMesh() {
        var model = GeckoLibCache.getBakedModels().get(MODEL);
        var bakedAnimations = GeckoLibCache.getBakedAnimations().get(ANIMATIONS);
        var animation = bakedAnimations == null ? null : bakedAnimations.getAnimation(IDLE_ANIMATION);
        if (model == null || animation == null) {
            if (MISSING_CACHE_WARNING.compareAndSet(false, true))
                CIFCommon.LOGGER.warn(
                        "Confluence duck model is not available in the GeckoLib cache yet; the duck portion of its roost model will be retried later");
            return null;
        }
        MISSING_CACHE_WARNING.set(false);
        var cached = meshCache;
        if (matches(cached, model, animation))
            return cached.mesh();
        var failed = failedCache;
        if (matches(failed, model, animation))
            return null;
        synchronized (CACHE_LOCK) {
            cached = meshCache;
            if (matches(cached, model, animation))
                return cached.mesh();
            failed = failedCache;
            if (matches(failed, model, animation))
                return null;
            try {
                var mesh = convert(model, animation);
                meshCache = new MeshCache(model, animation, mesh);
                failedCache = null;
                return mesh;
            } catch (RuntimeException exception) {
                failedCache = new FailedCache(model, animation);
                CIFCommon.LOGGER.error(
                        "Failed to bake the Confluence duck model; the base roost will remain visible",
                        exception);
                return null;
            }
        }
    }

    static List<BakedQuad> bake(
            Mesh mesh, TextureAtlasSprite sprite, Transformation modelTransform) {
        var transform = modelTransform.isIdentity()
                ? modelTransform
                : modelTransform.blockCenterToCorner();
        var positionTransform = new Matrix4f(transform.getMatrix());
        var normalTransform = new Matrix3f(transform.getNormalMatrix());
        var quads = new ArrayList<BakedQuad>(mesh.quads().size());
        for (var quad : mesh.quads()) {
            var normal = normalTransform.transform(new Vector3f(quad.normal())).normalize();
            var direction = Direction.getNearest(normal.x, normal.y, normal.z);
            var consumer = new QuadBakingVertexConsumer();
            consumer.setSprite(sprite);
            consumer.setTintIndex(-1);
            consumer.setShade(true);
            consumer.setHasAmbientOcclusion(false);
            for (var vertex : quad.vertices()) {
                var position = positionTransform.transformPosition(new Vector3f(vertex.position()));
                consumer.addVertex(position.x, position.y, position.z);
                consumer.setColor(255, 255, 255, 255);
                consumer.setUv(sprite.getU(vertex.u()), sprite.getV(vertex.v()));
                consumer.setNormal(normal.x, normal.y, normal.z);
            }
            consumer.setDirection(direction);
            quads.add(consumer.bakeQuad());
        }
        return List.copyOf(quads);
    }

    private static Mesh convert(BakedGeoModel model, Animation animation) {
        var animationsByBone = new HashMap<String, BoneAnimation>();
        for (var boneAnimation : animation.boneAnimations())
            animationsByBone.put(boneAnimation.boneName(), boneAnimation);
        var quads = new ArrayList<MeshQuad>();
        var rootTransform = new Matrix4f();
        for (var bone : model.topLevelBones())
            appendBone(bone, rootTransform, animationsByBone, quads);
        if (quads.isEmpty())
            throw new IllegalStateException("The Confluence duck model contains no visible quads");
        return placeInRoost(quads);
    }

    private static void appendBone(
            GeoBone bone,
            Matrix4f parentTransform,
            Map<String, BoneAnimation> animationsByBone,
            List<MeshQuad> output) {
        var pose = getPose(bone, animationsByBone.get(bone.getName()));
        var transform = new Matrix4f(parentTransform)
                .translate(-pose.positionX() / 16.0F, pose.positionY() / 16.0F, pose.positionZ() / 16.0F)
                .translate(
                        bone.getPivotX() / 16.0F,
                        bone.getPivotY() / 16.0F,
                        bone.getPivotZ() / 16.0F)
                .rotateZ(pose.rotationZ())
                .rotateY(pose.rotationY())
                .rotateX(pose.rotationX())
                .scale(pose.scaleX(), pose.scaleY(), pose.scaleZ())
                .translate(
                        -bone.getPivotX() / 16.0F,
                        -bone.getPivotY() / 16.0F,
                        -bone.getPivotZ() / 16.0F);
        if (!Boolean.TRUE.equals(bone.shouldNeverRender())) {
            for (var cube : bone.getCubes())
                appendCube(cube, transform, output);
        }
        for (var child : bone.getChildBones())
            appendBone(child, transform, animationsByBone, output);
    }

    private static void appendCube(GeoCube cube, Matrix4f boneTransform, List<MeshQuad> output) {
        var pivot = cube.pivot();
        var rotation = cube.rotation();
        var transform = new Matrix4f(boneTransform)
                .translate(
                        (float) (pivot.x / 16.0D),
                        (float) (pivot.y / 16.0D),
                        (float) (pivot.z / 16.0D))
                .rotateZ((float) rotation.z)
                .rotateY((float) rotation.y)
                .rotateX((float) rotation.x)
                .translate(
                        (float) (-pivot.x / 16.0D),
                        (float) (-pivot.y / 16.0D),
                        (float) (-pivot.z / 16.0D));
        var normalTransform = transform.normal(new Matrix3f());
        for (var quad : cube.quads()) {
            if (quad == null)
                continue;
            var normal = normalTransform.transform(new Vector3f(quad.normal()));
            RenderUtil.fixInvertedFlatCube(cube, normal);
            normal.normalize();
            var vertices = new ArrayList<MeshVertex>(quad.vertices().length);
            for (var vertex : quad.vertices()) {
                var position = transform.transformPosition(new Vector3f(vertex.position()));
                vertices.add(new MeshVertex(position, vertex.texU(), vertex.texV()));
            }
            output.add(new MeshQuad(List.copyOf(vertices), normal));
        }
    }

    private static BonePose getPose(GeoBone bone, BoneAnimation animation) {
        var initial = bone.getInitialSnapshot();
        if (initial == null)
            initial = new BoneSnapshot(bone);
        var rotationX = initial.getRotX();
        var rotationY = initial.getRotY();
        var rotationZ = initial.getRotZ();
        var positionX = initial.getOffsetX();
        var positionY = initial.getOffsetY();
        var positionZ = initial.getOffsetZ();
        var scaleX = initial.getScaleX();
        var scaleY = initial.getScaleY();
        var scaleZ = initial.getScaleZ();
        if (animation != null) {
            var rotation = animation.rotationKeyFrames();
            if (hasValues(rotation)) {
                rotationX += firstValue(rotation.xKeyframes());
                rotationY += firstValue(rotation.yKeyframes());
                rotationZ += firstValue(rotation.zKeyframes());
            }
            var position = animation.positionKeyFrames();
            if (hasValues(position)) {
                positionX = firstValue(position.xKeyframes());
                positionY = firstValue(position.yKeyframes());
                positionZ = firstValue(position.zKeyframes());
            }
            var scale = animation.scaleKeyFrames();
            if (hasValues(scale)) {
                scaleX = firstValue(scale.xKeyframes());
                scaleY = firstValue(scale.yKeyframes());
                scaleZ = firstValue(scale.zKeyframes());
            }
        }
        return new BonePose(
                rotationX,
                rotationY,
                rotationZ,
                positionX,
                positionY,
                positionZ,
                scaleX,
                scaleY,
                scaleZ);
    }

    private static boolean hasValues(KeyframeStack<? extends Keyframe<?>> stack) {
        return !stack.xKeyframes().isEmpty()
                && !stack.yKeyframes().isEmpty()
                && !stack.zKeyframes().isEmpty();
    }

    private static float firstValue(List<? extends Keyframe<? extends MathValue>> keyframes) {
        return (float) keyframes.getFirst().startValue().get();
    }

    private static Mesh placeInRoost(List<MeshQuad> quads) {
        var minX = Float.POSITIVE_INFINITY;
        var minY = Float.POSITIVE_INFINITY;
        var minZ = Float.POSITIVE_INFINITY;
        var maxX = Float.NEGATIVE_INFINITY;
        var maxZ = Float.NEGATIVE_INFINITY;
        for (var quad : quads) {
            for (var vertex : quad.vertices()) {
                var position = vertex.position();
                minX = Math.min(minX, position.x);
                minY = Math.min(minY, position.y);
                minZ = Math.min(minZ, position.z);
                maxX = Math.max(maxX, position.x);
                maxZ = Math.max(maxZ, position.z);
            }
        }
        var offsetX = 0.5F - (minX + maxX) * 0.5F;
        var offsetY = GROUND_HEIGHT - minY;
        var offsetZ = 0.5F - (minZ + maxZ) * 0.5F;
        var placedQuads = new ArrayList<MeshQuad>(quads.size());
        for (var quad : quads) {
            var placedVertices = new ArrayList<MeshVertex>(quad.vertices().size());
            for (var vertex : quad.vertices()) {
                var position = new Vector3f(vertex.position()).add(offsetX, offsetY, offsetZ);
                placedVertices.add(new MeshVertex(position, vertex.u(), vertex.v()));
            }
            placedQuads.add(new MeshQuad(List.copyOf(placedVertices), new Vector3f(quad.normal())));
        }
        return new Mesh(List.copyOf(placedQuads));
    }

    private static boolean matches(MeshCache cache, BakedGeoModel model, Animation animation) {
        return cache != null && cache.model() == model && cache.animation() == animation;
    }

    private static boolean matches(FailedCache cache, BakedGeoModel model, Animation animation) {
        return cache != null && cache.model() == model && cache.animation() == animation;
    }

    record Mesh(List<MeshQuad> quads) {}

    private record MeshQuad(List<MeshVertex> vertices, Vector3f normal) {}

    private record MeshVertex(Vector3f position, float u, float v) {}

    private record BonePose(
            float rotationX,
            float rotationY,
            float rotationZ,
            float positionX,
            float positionY,
            float positionZ,
            float scaleX,
            float scaleY,
            float scaleZ) {}

    private record MeshCache(BakedGeoModel model, Animation animation, Mesh mesh) {}

    private record FailedCache(BakedGeoModel model, Animation animation) {}
}
