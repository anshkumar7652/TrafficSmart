package com.trafficsmart.strategy;

import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;

/**
 * High-priority signal control strategy dedicated to emergency vehicle clearance.
 * Forces extended green phase durations to guarantee unimpeded transit through the intersection.
 *
 * @author Ansh
 * @version 1.0
 */
public class EmergencyPriorityStrategy implements SignalControlStrategy {

    private final int emergencyDurationSeconds;

    /**
     * Constructs an EmergencyPriorityStrategy with specific clearance duration.
     *
     * @param emergencyDurationSeconds green window duration (typically 30-45s)
     */
    public EmergencyPriorityStrategy(int emergencyDurationSeconds) {
        this.emergencyDurationSeconds = Math.max(20, emergencyDurationSeconds);
    }

    /**
     * Default constructor with 35-second green window.
     */
    public EmergencyPriorityStrategy() {
        this(35);
    }

    @Override
    public int calculateGreenDuration(SensorData data, Intersection intersection) {
        // Emergency overrides provide fixed guaranteed green time regardless of standard traffic metrics
        return emergencyDurationSeconds;
    }
}
