package com.trafficsmart.strategy;

import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;

/**
 * Adaptive signal control strategy that dynamically scales green light duration
 * in proportion to observed vehicle density and average vehicle speed.
 *
 * @author Ansh
 * @version 1.0
 */
public class DynamicDensityStrategy implements SignalControlStrategy {

    private final int minGreenSeconds;
    private final int maxGreenSeconds;

    /**
     * Constructs a DynamicDensityStrategy with configurable minimum and maximum bounds.
     *
     * @param minGreenSeconds lower duration bound
     * @param maxGreenSeconds upper duration bound
     */
    public DynamicDensityStrategy(int minGreenSeconds, int maxGreenSeconds) {
        this.minGreenSeconds = Math.max(5, minGreenSeconds);
        this.maxGreenSeconds = Math.max(this.minGreenSeconds, maxGreenSeconds);
    }

    /**
     * Default constructor with typical municipal bounds (10s to 60s).
     */
    public DynamicDensityStrategy() {
        this(10, 60);
    }

    @Override
    public int calculateGreenDuration(SensorData data, Intersection intersection) {
        if (data == null) {
            return minGreenSeconds;
        }

        int count = data.getVehicleCount();
        double speed = data.getAvgSpeed();

        // Heuristic: More vehicles = longer green; slower traffic (congestion) = extended clearance window
        double densityFactor = Math.min(1.0, count / 30.0);
        double speedPenaltyFactor = (speed < 15.0) ? 1.25 : 1.0;

        int calculated = (int) Math.round(minGreenSeconds + (maxGreenSeconds - minGreenSeconds) * densityFactor * speedPenaltyFactor);
        return Math.max(minGreenSeconds, Math.min(maxGreenSeconds, calculated));
    }
}
