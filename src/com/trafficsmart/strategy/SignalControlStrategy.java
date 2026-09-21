package com.trafficsmart.strategy;

import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;

/**
 * Functional interface defining the dynamic strategy contract for calculating signal green-light durations.
 * Demonstrates the Strategy design pattern and Java functional interfaces compatible with lambdas.
 *
 * @author Ansh
 * @version 1.0
 */
@FunctionalInterface
public interface SignalControlStrategy {

    /**
     * Calculates the optimal green light duration (in seconds) based on sensor telemetry and intersection state.
     *
     * @param data real-time sensor metrics
     * @param intersection current intersection context
     * @return calculated duration in seconds
     */
    int calculateGreenDuration(SensorData data, Intersection intersection);
}
