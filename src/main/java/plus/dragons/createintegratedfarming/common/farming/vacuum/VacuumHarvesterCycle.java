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

import net.minecraft.util.Mth;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public final class VacuumHarvesterCycle {
    public static final String CHARGE_PROGRESS = "ChargeProgress";
    public static final String RELEASE_TICKS = "ReleaseTicks";
    public static final int RELEASE_DURATION = 5;
    public static final float MAX_HEAD_OFFSET = 6.0F / 16.0F;
    private static final int REFERENCE_RPM = 64;
    private static final int MINIMUM_CHARGE_TICKS = 20;

    private VacuumHarvesterCycle() {}

    public static double stationaryChargeIncrement(float speed) {
        if (speed == 0)
            return 0;
        double configuredTicks = CIFConfig.server().vacuumHarvesterChargeTime.get();
        double chargeTicks = Math.max(
                MINIMUM_CHARGE_TICKS,
                Math.ceil(configuredTicks * REFERENCE_RPM / Math.abs(speed)));
        return 1.0 / chargeTicks;
    }

    public static double contraptionChargeIncrement() {
        return 1.0 / CIFConfig.server().vacuumHarvesterChargeTime.get();
    }

    public static double advanceCharge(double chargeProgress, double increment) {
        double next = Mth.clamp(chargeProgress + increment, 0, 1);
        return next >= 1 - 1.0E-9 ? 1 : next;
    }

    public static float getHeadOffset(double chargeProgress, int releaseTicks) {
        if (releaseTicks > 0) {
            float remaining = Mth.clamp(releaseTicks / (float) RELEASE_DURATION, 0, 1);
            return MAX_HEAD_OFFSET * remaining * remaining;
        }
        float progress = (float) Mth.clamp(chargeProgress, 0, 1);
        float eased = progress * progress * (3 - 2 * progress);
        return MAX_HEAD_OFFSET * eased;
    }
}
