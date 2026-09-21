package com.trafficsmart.model;

import java.util.Map;

/**
 * Common monitoring interface for all active telemetry elements (signals, intersections).
 * Demonstrates modern Java interface capabilities including default methods.
 *
 * @author Ansh
 * @version 1.0
 */
public interface Monitorable {

    /**
     * Gets a short summary of the current operational status.
     *
     * @return status string representation
     */
    String getStatus();

    /**
     * Retrieves key operational metrics in a structured key-value mapping.
     *
     * @return map of metric names to values
     */
    Map<String, Object> getMetrics();

    /**
     * Returns the timestamp (in epoch milliseconds) when this component was last updated.
     *
     * @return timestamp in milliseconds
     */
    long getLastUpdated();

    /**
     * Default interface method to determine if the monitored component is in a healthy operating state.
     * Subclasses can override if custom diagnostic heuristics are needed.
     *
     * @return true if healthy, false otherwise
     */
    default boolean isHealthy() {
        return (System.currentTimeMillis() - getLastUpdated()) < 60000;
    }
}
