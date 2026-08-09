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

package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.simibubi.create.foundation.particle.AirParticleData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VacuumHarvesterEffects {
    private static final AirParticleData EXHAUST = new AirParticleData(1.0F, 0.08F);
    private static final AirParticleData SUCTION = new AirParticleData(2.0F, 0.35F);
    private static final int AMBIENT_SUCTION_STREAMS = 32;
    private static final int HARVEST_SUCTION_STREAMS = 2;
    private static final double IDLE_INTAKE_Y_OFFSET = 7.0 / 16.0;

    private VacuumHarvesterEffects() {}

    public static Vec3 intake(BlockPos pos, float headOffset) {
        return intake(Vec3.atCenterOf(pos), headOffset);
    }

    public static Vec3 intake(Vec3 center, float headOffset) {
        return center.add(0, IDLE_INTAKE_Y_OFFSET - headOffset, 0);
    }

    public static void spawnExhaust(Level level, Vec3 intake) {
        double angle = level.random.nextDouble() * Math.PI * 2;
        double distance = 0.5 + level.random.nextDouble() * 0.25;
        Vec3 target = intake.add(
                Math.cos(angle) * distance,
                0.1 + level.random.nextDouble() * 0.2,
                Math.sin(angle) * distance);
        level.addParticle(EXHAUST, intake.x, intake.y, intake.z,
                target.x - intake.x, target.y - intake.y, target.z - intake.z);
    }

    public static void emitSuction(ServerLevel level, Vec3 intake, List<BlockPos> harvestedPositions) {
        double phase = level.random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < AMBIENT_SUCTION_STREAMS; i++) {
            double angle = phase
                    + i * Math.PI * 2 / AMBIENT_SUCTION_STREAMS
                    + (level.random.nextDouble() - 0.5) * 0.16;
            double radius = 2.25 + level.random.nextDouble() * 2.25;
            double height = level.random.nextDouble() * 1.6 - 0.45;
            Vec3 source = intake.add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            sendAir(level, source, intake);
        }
        for (BlockPos pos : harvestedPositions) {
            for (int i = 0; i < HARVEST_SUCTION_STREAMS; i++) {
                Vec3 source = Vec3.atCenterOf(pos).add(
                        level.random.nextDouble() * 0.7 - 0.35,
                        0.1 + level.random.nextDouble() * 0.8,
                        level.random.nextDouble() * 0.7 - 0.35);
                sendAir(level, source, intake);
            }
        }
    }

    private static void sendAir(ServerLevel level, Vec3 source, Vec3 target) {
        level.sendParticles(
                SUCTION,
                source.x,
                source.y,
                source.z,
                0,
                target.x - source.x,
                target.y - source.y,
                target.z - source.z,
                1);
    }
}
