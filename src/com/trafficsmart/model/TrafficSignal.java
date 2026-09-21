package com.trafficsmart.model;

import com.trafficsmart.exception.SignalFailureException;
import com.trafficsmart.strategy.DynamicDensityStrategy;
import com.trafficsmart.strategy.EmergencyPriorityStrategy;
import com.trafficsmart.strategy.SignalControlStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a physical or simulated traffic light controlling a specific lane approach at an intersection.
 * Implements {@link Monitorable} and uses thread-safe synchronization for state mutations.
 *
 * @author Ansh
 * @version 1.0
 */
public class TrafficSignal implements Monitorable {

    private final String signalId;
    private final String intersectionId;
    private final String approachDirection; // NORTH, SOUTH, EAST, WEST
    private SignalState currentState;
    private int currentDurationSeconds;
    private SignalControlStrategy strategy;
    private long lastStateChangeTimestamp;
    private boolean manualOverrideActive;

    /**
     * Constructs a new TrafficSignal with default RED state and DynamicDensityStrategy.
     *
     * @param signalId unique signal ID
     * @param intersectionId parent intersection ID
     * @param approachDirection cardinal approach (e.g. "NORTH")
     */
    public TrafficSignal(String signalId, String intersectionId, String approachDirection) {
        this.signalId = Objects.requireNonNull(signalId, "Signal ID cannot be null");
        this.intersectionId = Objects.requireNonNull(intersectionId, "Intersection ID cannot be null");
        this.approachDirection = (approachDirection != null) ? approachDirection.toUpperCase() : "NORTH";
        this.currentState = SignalState.RED;
        this.strategy = new DynamicDensityStrategy();
        this.currentDurationSeconds = currentState.getDefaultDurationSeconds();
        this.lastStateChangeTimestamp = System.currentTimeMillis();
        this.manualOverrideActive = false;
    }

    /**
     * Synchronized method to transition this signal to its next natural state in sequence.
     * GREEN -> YELLOW -> RED -> GREEN.
     * Throws {@link SignalFailureException} if currentState is null.
     */
    public synchronized void cycleNext() {
        if (currentState == null) {
            throw new SignalFailureException("Signal current state is uninitialized/null", signalId);
        }
        SignalState nextState = currentState.next();
        setState(nextState, nextState.getDefaultDurationSeconds());
    }

    /**
     * Synchronized state mutation with custom duration.
     *
     * @param newState target state
     * @param durationSeconds duration in seconds
     */
    public synchronized void setState(SignalState newState, int durationSeconds) {
        if (newState == null) {
            throw new SignalFailureException("Cannot transition to null SignalState", signalId);
        }
        this.currentState = newState;
        this.currentDurationSeconds = Math.max(1, durationSeconds);
        this.lastStateChangeTimestamp = System.currentTimeMillis();
    }

    /**
     * Immediately overrides this signal to GREEN for emergency vehicle transit.
     * Applies the {@link EmergencyPriorityStrategy}.
     */
    public synchronized void overrideToGreen() {
        this.strategy = new EmergencyPriorityStrategy();
        this.manualOverrideActive = true;
        setState(SignalState.GREEN, 35);
    }

    /**
     * Restores automatic adaptive dynamic strategy and clears manual overrides.
     */
    public synchronized void clearOverride() {
        this.manualOverrideActive = false;
        this.strategy = new DynamicDensityStrategy();
    }

    /**
     * Recalculates and updates green duration using the configured strategy and sensor reading.
     *
     * @param data sensor telemetry
     * @param intersection intersection context
     */
    public synchronized void adaptDuration(SensorData data, Intersection intersection) {
        if (!manualOverrideActive && strategy != null) {
            int calculatedDuration = strategy.calculateGreenDuration(data, intersection);
            if (currentState == SignalState.GREEN) {
                this.currentDurationSeconds = calculatedDuration;
            }
        }
    }

    public synchronized SignalState getCurrentState() {
        return currentState;
    }

    public synchronized int getCurrentDurationSeconds() {
        return currentDurationSeconds;
    }

    public synchronized void setStrategy(SignalControlStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    public String getSignalId() {
        return signalId;
    }

    public String getIntersectionId() {
        return intersectionId;
    }

    public String getApproachDirection() {
        return approachDirection;
    }

    public boolean isManualOverrideActive() {
        return manualOverrideActive;
    }

    @Override
    public synchronized String getStatus() {
        return String.format("[%s @ %s - %s] State=%s, Duration=%ds, Override=%b",
                signalId, intersectionId, approachDirection, currentState, currentDurationSeconds, manualOverrideActive);
    }

    @Override
    public synchronized Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("signalId", signalId);
        metrics.put("intersectionId", intersectionId);
        metrics.put("direction", approachDirection);
        metrics.put("state", currentState.name());
        metrics.put("durationSeconds", currentDurationSeconds);
        metrics.put("override", manualOverrideActive);
        metrics.put("lastChangedMs", lastStateChangeTimestamp);
        return metrics;
    }

    @Override
    public synchronized long getLastUpdated() {
        return lastStateChangeTimestamp;
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
