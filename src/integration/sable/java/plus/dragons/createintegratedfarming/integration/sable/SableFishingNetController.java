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

package plus.dragons.createintegratedfarming.integration.sable;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetContext;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetMovementBehaviour;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public final class SableFishingNetController {
    private static final double MOVEMENT_EPSILON_SQUARED = 1.0E-12;
    private static final long PARENT_LAYER_SALT = 0x61C8864680B583EBL;
    private static final Direction[] DIRECTIONS = Direction.values();

    private final ServerSubLevel sourceSubLevel;
    private final ServerLevel level;
    private final Reference2ObjectOpenHashMap<AbstractFishingNetMovementBehaviour<?>, NetGroup> groups = new Reference2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<AbstractFishingNetMovementBehaviour<?>> behavioursByPosition = new Long2ObjectOpenHashMap<>();
    private final ObjectArrayList<ServerSubLevel> movingTargets = new ObjectArrayList<>();
    private final ReferenceOpenHashSet<ServerSubLevel> candidateSet = new ReferenceOpenHashSet<>();
    private final Long2ObjectOpenHashMap<Panel> panelsByPosition = new Long2ObjectOpenHashMap<>();
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos receiverPos = new BlockPos.MutableBlockPos();
    private final Vector3d localPoint = new Vector3d();
    private final Vector3d oldGlobalPoint = new Vector3d();
    private final Vector3d currentGlobalPoint = new Vector3d();
    private final Vector3d oldRelativePoint = new Vector3d();
    private final Vector3d currentRelativePoint = new Vector3d();
    private final Pose3d lastCapturePose;
    private @Nullable AABB localBounds;
    private boolean boundsDirty;
    private boolean topologyDirty = true;
    private boolean parentMovesRelative;

    public SableFishingNetController(ServerLevelPlot plot) {
        this.sourceSubLevel = plot.getSubLevel();
        this.level = sourceSubLevel.getLevel();
        this.lastCapturePose = new Pose3d(sourceSubLevel.logicalPose());
    }

    public void onBlockChange(BlockPos pos, @Nullable AbstractFishingNetMovementBehaviour<?> behaviour, BlockState state) {
        topologyDirty = true;
        long packedPos = pos.asLong();
        AbstractFishingNetMovementBehaviour<?> oldBehaviour = behavioursByPosition.get(packedPos);
        if (oldBehaviour == behaviour)
            return;
        boolean wasFishingNet = oldBehaviour != null;
        boolean isFishingNet = behaviour != null;
        if (oldBehaviour != null) {
            NetGroup oldGroup = groups.get(oldBehaviour);
            if (oldGroup != null) {
                oldGroup.remove(packedPos);
                if (oldGroup.positions.isEmpty()) {
                    oldGroup.close();
                    groups.remove(oldBehaviour);
                }
            }
            behavioursByPosition.remove(packedPos);
        }
        if (behaviour != null) {
            NetGroup group = groups.get(behaviour);
            if (group == null) {
                group = new NetGroup(behaviour, pos, state);
                groups.put(behaviour, group);
            }
            group.add(packedPos);
            behavioursByPosition.put(packedPos, behaviour);
        }
        if (!wasFishingNet && isFishingNet)
            expandBounds(pos);
        else if (wasFishingNet && !isFishingNet)
            markBoundsDirtyIfBoundary(pos);
    }

    public boolean isEmpty() {
        return behavioursByPosition.isEmpty();
    }

    public void tick() {
        if (isEmpty())
            return;
        if (localBounds == null)
            ensureBounds();
        if (localBounds == null)
            return;

        long gameTime = level.getGameTime();
        buildMovingTargetList();
        if (parentMovesRelative || !movingTargets.isEmpty())
            tickFishing(gameTime);
        if (gameTime % 20 == 0 && CIFConfig.server().fishingNetCaptureCreatureInWater.get()) {
            ensureBounds();
            if (localBounds != null)
                captureCreatures();
        }
    }

    public void close() {
        for (NetGroup group : groups.values())
            group.close();
        groups.clear();
        behavioursByPosition.clear();
        panelsByPosition.clear();
    }

    private void expandBounds(BlockPos pos) {
        if (localBounds == null) {
            localBounds = new AABB(pos);
            return;
        }
        localBounds = new AABB(
                Math.min(localBounds.minX, pos.getX()),
                Math.min(localBounds.minY, pos.getY()),
                Math.min(localBounds.minZ, pos.getZ()),
                Math.max(localBounds.maxX, pos.getX() + 1),
                Math.max(localBounds.maxY, pos.getY() + 1),
                Math.max(localBounds.maxZ, pos.getZ() + 1));
    }

    private void markBoundsDirtyIfBoundary(BlockPos pos) {
        if (localBounds != null
                && (pos.getX() == localBounds.minX
                        || pos.getY() == localBounds.minY
                        || pos.getZ() == localBounds.minZ
                        || pos.getX() + 1 == localBounds.maxX
                        || pos.getY() + 1 == localBounds.maxY
                        || pos.getZ() + 1 == localBounds.maxZ))
            boundsDirty = true;
    }

    private void ensureBounds() {
        if (!boundsDirty && localBounds != null)
            return;
        boundsDirty = false;
        if (behavioursByPosition.isEmpty()) {
            localBounds = null;
            return;
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (long packedPos : behavioursByPosition.keySet()) {
            int x = BlockPos.getX(packedPos);
            int y = BlockPos.getY(packedPos);
            int z = BlockPos.getZ(packedPos);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        localBounds = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
    }

    private void buildMovingTargetList() {
        movingTargets.clear();
        candidateSet.clear();
        parentMovesRelative = poseChanged(sourceSubLevel.lastPose(), sourceSubLevel.logicalPose());

        BoundingBox3d globalBounds = new BoundingBox3d(localBounds).transform(sourceSubLevel.lastPose());
        globalBounds.expandTo(new BoundingBox3d(localBounds).transform(sourceSubLevel.logicalPose()));
        for (SubLevel candidate : Sable.HELPER.getAllIntersecting(level, globalBounds)) {
            if (candidate == sourceSubLevel
                    || !(candidate instanceof ServerSubLevel serverCandidate)
                    || !candidateSet.add(serverCandidate))
                continue;
            if (relativePoseChanged(serverCandidate))
                movingTargets.add(serverCandidate);
        }
    }

    private boolean relativePoseChanged(ServerSubLevel target) {
        return relativePointChanged(target, 0, 0, 0)
                || relativePointChanged(target, 1, 0, 0)
                || relativePointChanged(target, 0, 1, 0)
                || relativePointChanged(target, 0, 0, 1);
    }

    private boolean relativePointChanged(ServerSubLevel target, double x, double y, double z) {
        localPoint.set(x, y, z);
        sourceSubLevel.lastPose().transformPosition(localPoint, oldGlobalPoint);
        sourceSubLevel.logicalPose().transformPosition(localPoint, currentGlobalPoint);
        target.lastPose().transformPositionInverse(oldGlobalPoint, oldRelativePoint);
        target.logicalPose().transformPositionInverse(currentGlobalPoint, currentRelativePoint);
        return oldRelativePoint.distanceSquared(currentRelativePoint) > MOVEMENT_EPSILON_SQUARED;
    }

    private static boolean poseChanged(Pose3dc previous, Pose3dc current) {
        return !previous.position().equals(current.position())
                || !previous.orientation().equals(current.orientation())
                || !previous.rotationPoint().equals(current.rotationPoint())
                || !previous.scale().equals(current.scale());
    }

    private void tickFishing(long gameTime) {
        for (NetGroup group : groups.values()) {
            LongIterator iterator = group.positions.iterator();
            while (iterator.hasNext()) {
                long netPos = iterator.nextLong();
                int maxRecorded = CIFConfig.server().fishingNetMaxRecordedBlocks.get();
                LongOpenHashSet visited = group.visitedBlocks.get(netPos);
                if (visited != null && visited.size() >= maxRecorded && gameTime < group.nextCatchTime.get(netPos))
                    continue;

                boolean foundInParent = parentMovesRelative && traceMovement(group, netPos, null, gameTime);
                if (foundInParent)
                    continue;
                for (int i = 0, size = movingTargets.size(); i < size; i++)
                    traceMovement(group, netPos, movingTargets.get(i), gameTime);
            }
        }
    }

    private boolean traceMovement(
            NetGroup group, long netPos, @Nullable ServerSubLevel targetSubLevel, long gameTime) {
        double x = BlockPos.getX(netPos) + 0.5;
        double y = BlockPos.getY(netPos) + 0.5;
        double z = BlockPos.getZ(netPos) + 0.5;
        localPoint.set(x, y, z);
        sourceSubLevel.lastPose().transformPosition(localPoint, oldGlobalPoint);
        sourceSubLevel.logicalPose().transformPosition(localPoint, currentGlobalPoint);
        if (targetSubLevel == null) {
            oldRelativePoint.set(oldGlobalPoint);
            currentRelativePoint.set(currentGlobalPoint);
        } else {
            targetSubLevel.lastPose().transformPositionInverse(oldGlobalPoint, oldRelativePoint);
            targetSubLevel.logicalPose().transformPositionInverse(currentGlobalPoint, currentRelativePoint);
        }
        return visitVoxels(group, netPos, targetSubLevel, oldRelativePoint, currentRelativePoint, gameTime);
    }

    private boolean visitVoxels(
            NetGroup group,
            long netPos,
            @Nullable ServerSubLevel targetSubLevel,
            Vector3d start,
            Vector3d end,
            long gameTime) {
        int x = Mth.floor(start.x);
        int y = Mth.floor(start.y);
        int z = Mth.floor(start.z);
        int endX = Mth.floor(end.x);
        int endY = Mth.floor(end.y);
        int endZ = Mth.floor(end.z);
        if (x == endX && y == endY && z == endZ)
            return false;

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;
        int stepX = Integer.compare(endX, x);
        int stepY = Integer.compare(endY, y);
        int stepZ = Integer.compare(endZ, z);
        double deltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dx);
        double deltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dy);
        double deltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dz);
        double maxX = stepX > 0 ? (x + 1 - start.x) / dx : stepX < 0 ? (start.x - x) / -dx : Double.POSITIVE_INFINITY;
        double maxY = stepY > 0 ? (y + 1 - start.y) / dy : stepY < 0 ? (start.y - y) / -dy : Double.POSITIVE_INFINITY;
        double maxZ = stepZ > 0 ? (z + 1 - start.z) / dz : stepZ < 0 ? (start.z - z) / -dz : Double.POSITIVE_INFINITY;
        boolean foundValid = false;
        while (x != endX || y != endY || z != endZ) {
            double next = Math.min(maxX, Math.min(maxY, maxZ));
            if (maxX <= next + 1.0E-12) {
                x += stepX;
                maxX += deltaX;
            }
            if (maxY <= next + 1.0E-12) {
                y += stepY;
                maxY += deltaY;
            }
            if (maxZ <= next + 1.0E-12) {
                z += stepZ;
                maxZ += deltaZ;
            }
            if (visitFluid(group, netPos, targetSubLevel, x, y, z, gameTime))
                foundValid = true;
        }
        return foundValid;
    }

    private boolean visitFluid(
            NetGroup group,
            long netPos,
            @Nullable ServerSubLevel targetSubLevel,
            int x,
            int y,
            int z,
            long gameTime) {
        mutablePos.set(x, y, z);
        if (!level.isLoaded(mutablePos) || !group.context.isPosValidForFishing(level, mutablePos))
            return false;

        int maxRecorded = CIFConfig.server().fishingNetMaxRecordedBlocks.get();
        LongOpenHashSet visited = group.visitedBlocks.get(netPos);
        if (visited == null) {
            visited = new LongOpenHashSet(Math.min(16, maxRecorded));
            group.visitedBlocks.put(netPos, visited);
        }
        if (visited.size() < maxRecorded)
            visited.add(visitedKey(targetSubLevel, mutablePos.asLong()));
        if (gameTime < group.nextCatchTime.get(netPos))
            return true;

        if (group.context.getFishingHook().getRandom().nextInt(maxRecorded) < visited.size())
            catchFish(group, netPos, mutablePos.immutable());
        group.visitedBlocks.remove(netPos);
        group.nextCatchTime.put(netPos, gameTime + group.context.getCatchDelay(level));
        return true;
    }

    private static long visitedKey(@Nullable ServerSubLevel targetSubLevel, long packedPos) {
        long salt = targetSubLevel == null
                ? PARENT_LAYER_SALT
                : (Integer.toUnsignedLong(targetSubLevel.getRuntimeId()) + 1) * 0x9E3779B97F4A7C15L;
        long value = packedPos ^ salt;
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        return value ^ value >>> 33;
    }

    private void catchFish(NetGroup group, long netPos, BlockPos fishingPos) {
        prepareContext(group, netPos);
        group.movementContext.position = fishingPos.getCenter();
        var catchContext = group.context.buildFishingCatchContext(group.movementContext, level, fishingPos);
        List<ItemStack> loots = new ArrayList<>(FishingNetCatchProviders.getCatch(catchContext));
        var event = NeoForge.EVENT_BUS.post(new ItemFishedEvent(loots, 0, group.context.getFishingHook()));
        if (!event.isCanceled())
            loots.forEach(stack -> output(group, netPos, stack));
    }

    private void prepareContext(NetGroup group, long netPos) {
        mutablePos.set(BlockPos.getX(netPos), BlockPos.getY(netPos), BlockPos.getZ(netPos));
        group.movementContext.localPos = mutablePos.immutable();
        group.movementContext.state = level.getBlockState(mutablePos);
        group.movementContext.position = mutablePos.getCenter();
        Vector3d velocity = Sable.HELPER.getVelocity(level, sourceSubLevel, localPoint.set(group.movementContext.position.x, group.movementContext.position.y, group.movementContext.position.z), currentGlobalPoint);
        group.movementContext.motion = new Vec3(velocity.x / 20.0, velocity.y / 20.0, velocity.z / 20.0);
    }

    private void captureCreatures() {
        BoundingBox3d globalSweep = new BoundingBox3d(localBounds).transform(lastCapturePose);
        globalSweep.expandTo(new BoundingBox3d(localBounds).transform(sourceSubLevel.logicalPose()));
        AABB queryBounds = globalSweep.toMojang().inflate(0.2);
        List<LivingEntity> candidates = level.getEntities(
                EntityTypeTest.forClass(LivingEntity.class), queryBounds, entity -> true);
        IntOpenHashSet seen = new IntOpenHashSet(candidates.size());
        for (LivingEntity entity : candidates) {
            if (!seen.add(entity.getId()) || !entity.isAlive())
                continue;
            captureCreature(entity);
        }
        lastCapturePose.set(sourceSubLevel.logicalPose());
    }

    private void captureCreature(LivingEntity entity) {
        AABB entityBounds = entity.getBoundingBox();
        AABB oldEntityBounds = entityBounds.move(
                entity.xo - entity.getX(), entity.yo - entity.getY(), entity.zo - entity.getZ());
        SubLevel tracked = Sable.HELPER.getTrackingOrVehicleSubLevel(entity);
        BoundingBox3d globalEntityBounds = new BoundingBox3d(entityBounds);
        BoundingBox3d oldGlobalEntityBounds = new BoundingBox3d(oldEntityBounds);
        if (tracked != null) {
            globalEntityBounds.transform(tracked.logicalPose());
            oldGlobalEntityBounds.transform(tracked.lastPose());
        }
        globalEntityBounds.expandTo(oldGlobalEntityBounds);
        BoundingBox3d localEntityBounds = new BoundingBox3d(globalEntityBounds).transformInverse(sourceSubLevel.logicalPose());
        localEntityBounds.expandTo(new BoundingBox3d(globalEntityBounds).transformInverse(lastCapturePose));
        AABB sweptLocalBounds = localEntityBounds.toMojang().inflate(0.2);

        int minX = Math.max(Mth.floor(sweptLocalBounds.minX), Mth.floor(localBounds.minX));
        int minY = Math.max(Mth.floor(sweptLocalBounds.minY), Mth.floor(localBounds.minY));
        int minZ = Math.max(Mth.floor(sweptLocalBounds.minZ), Mth.floor(localBounds.minZ));
        int maxX = Math.min(Mth.floor(sweptLocalBounds.maxX), Mth.floor(localBounds.maxX));
        int maxY = Math.min(Mth.floor(sweptLocalBounds.maxY), Mth.floor(localBounds.maxY));
        int maxZ = Math.min(Mth.floor(sweptLocalBounds.maxZ), Mth.floor(localBounds.maxZ));
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mutablePos.set(x, y, z);
                    long netPos = mutablePos.asLong();
                    AbstractFishingNetMovementBehaviour<?> behaviour = behavioursByPosition.get(netPos);
                    if (behaviour == null || !behaviour.canCaptureEntity(entity))
                        continue;
                    BlockState state = level.getBlockState(mutablePos);
                    AABB netShape = state.getShape(level, mutablePos).bounds().move(mutablePos).inflate(0.2);
                    if (!netShape.intersects(sweptLocalBounds))
                        continue;
                    onCaptureCreature(groups.get(behaviour), netPos, entity);
                    return;
                }
            }
        }
    }

    private void onCaptureCreature(NetGroup group, long netPos, LivingEntity entity) {
        prepareContext(group, netPos);
        group.movementContext.position = entity.position();
        if (!entity.isBaby() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(entity.getLootTable());
            var lootParams = group.context.buildCaptureLootContext(group.movementContext, level, entity);
            lootTable.getRandomItems(lootParams, entity.getLootTableSeed(), stack -> output(group, netPos, stack));
            if (CIFConfig.server().fishingNetCapturedCreatureDropExpNugget.get()) {
                int experience = EventHooks.getExperienceDrop(
                        entity,
                        group.context.getPlayer(),
                        entity.getExperienceReward(level, entity));
                output(group, netPos, new ItemStack(AllItems.EXP_NUGGET.get(), Math.ceilDiv(experience, 3)));
            }
        }
        entity.discard();
    }

    private void output(NetGroup group, long netPos, ItemStack stack) {
        if (stack.isEmpty())
            return;
        ItemStack remainder = stack;
        Panel panel = getPanel(netPos);
        if (panel != null)
            remainder = panel.insert(remainder);
        if (!remainder.isEmpty()) {
            group.movementContext.position = group.movementContext.localPos.getCenter();
            group.behaviour.collectOrDropItem(group.movementContext, remainder);
        }
    }

    private @Nullable Panel getPanel(long rootPos) {
        if (topologyDirty) {
            topologyDirty = false;
            panelsByPosition.clear();
        }
        Panel cached = panelsByPosition.get(rootPos);
        if (cached != null)
            return cached;
        if (!behavioursByPosition.containsKey(rootPos))
            return null;

        mutablePos.set(BlockPos.getX(rootPos), BlockPos.getY(rootPos), BlockPos.getZ(rootPos));
        BlockState rootState = level.getBlockState(mutablePos);
        if (!rootState.hasProperty(BlockStateProperties.FACING))
            return null;
        Direction.Axis axis = rootState.getValue(BlockStateProperties.FACING).getAxis();
        Panel panel = new Panel();
        LongArrayFIFOQueue frontier = new LongArrayFIFOQueue();
        LongOpenHashSet visited = new LongOpenHashSet();
        frontier.enqueue(rootPos);
        visited.add(rootPos);
        while (!frontier.isEmpty()) {
            long packedPos = frontier.dequeueLong();
            panel.positions.add(packedPos);
            int x = BlockPos.getX(packedPos);
            int y = BlockPos.getY(packedPos);
            int z = BlockPos.getZ(packedPos);
            mutablePos.set(x, y, z);
            collectReceivers(panel, mutablePos);
            for (Direction direction : DIRECTIONS) {
                if (direction.getAxis() == axis)
                    continue;
                mutablePos.set(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
                long neighbour = mutablePos.asLong();
                if (visited.contains(neighbour) || !isCoplanarNet(neighbour, axis))
                    continue;
                visited.add(neighbour);
                frontier.enqueue(neighbour);
            }
        }
        for (long position : panel.positions)
            panelsByPosition.put(position, panel);
        return panel;
    }

    private boolean isCoplanarNet(long packedPos, Direction.Axis axis) {
        if (!behavioursByPosition.containsKey(packedPos))
            return false;
        mutablePos.set(BlockPos.getX(packedPos), BlockPos.getY(packedPos), BlockPos.getZ(packedPos));
        BlockState state = level.getBlockState(mutablePos);
        return state.hasProperty(BlockStateProperties.FACING)
                && state.getValue(BlockStateProperties.FACING).getAxis() == axis;
    }

    private void collectReceivers(Panel panel, BlockPos netPos) {
        for (Direction direction : DIRECTIONS) {
            receiverPos.setWithOffset(netPos, direction);
            var blockEntity = level.getBlockEntity(receiverPos);
            if (blockEntity instanceof SableFishingNetItemReceiver receiver && panel.receiverSet.add(receiver))
                panel.receivers.add(new Receiver(receiver, netPos.immutable()));
        }
    }

    private final class NetGroup {
        private final AbstractFishingNetMovementBehaviour<?> behaviour;
        private final AbstractFishingNetContext<?> context;
        private final MovementContext movementContext;
        private final LongOpenHashSet positions = new LongOpenHashSet();
        private final Long2LongOpenHashMap nextCatchTime = new Long2LongOpenHashMap();
        private final Long2ObjectOpenHashMap<LongOpenHashSet> visitedBlocks = new Long2ObjectOpenHashMap<>();

        private NetGroup(AbstractFishingNetMovementBehaviour<?> behaviour, BlockPos initialPos, BlockState state) {
            this.behaviour = behaviour;
            this.context = behaviour.createFishingNetContext(level);
            this.movementContext = new MovementContext(
                    level,
                    new StructureBlockInfo(initialPos.immutable(), state, new CompoundTag()),
                    null);
        }

        private void add(long packedPos) {
            positions.add(packedPos);
            nextCatchTime.put(packedPos, level.getGameTime() + context.getCatchDelay(level));
        }

        private void remove(long packedPos) {
            positions.remove(packedPos);
            nextCatchTime.remove(packedPos);
            visitedBlocks.remove(packedPos);
        }

        private void close() {
            context.invalidate(level);
            positions.clear();
            nextCatchTime.clear();
            visitedBlocks.clear();
        }
    }

    private final class Panel {
        private final LongOpenHashSet positions = new LongOpenHashSet();
        private final ObjectArrayList<Receiver> receivers = new ObjectArrayList<>();
        private final ReferenceOpenHashSet<SableFishingNetItemReceiver> receiverSet = new ReferenceOpenHashSet<>();
        private int nextReceiver;

        private ItemStack insert(ItemStack stack) {
            if (receivers.isEmpty())
                return stack;
            ItemStack remainder = stack;
            int size = receivers.size();
            int start = Math.floorMod(nextReceiver, size);
            for (int offset = 0; offset < size && !remainder.isEmpty(); offset++) {
                int index = (start + offset) % size;
                Receiver entry = receivers.get(index);
                if (!entry.receiver.createintegratedfarming$isUsable()
                        || !entry.receiver.createintegratedfarming$acceptsFrom(entry.netPos))
                    continue;
                int previousCount = remainder.getCount();
                remainder = entry.receiver.createintegratedfarming$receive(remainder, entry.netPos);
                if (remainder.getCount() < previousCount)
                    nextReceiver = (index + 1) % size;
            }
            return remainder;
        }
    }

    private record Receiver(SableFishingNetItemReceiver receiver, BlockPos netPos) {}
}
