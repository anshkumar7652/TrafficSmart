package com.trafficsmart.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class representing a generic vehicle operating within the traffic monitoring system.
 * Implements {@link Comparable} for priority-based scheduling and {@link Serializable} for state backup.
 * Demonstrates Template Method pattern, abstract methods, and robust encapsulation.
 *
 * @author Ansh
 * @version 1.0
 */
public abstract class Vehicle implements Comparable<Vehicle>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String plateNumber;
    private final String type;
    private double speed;
    private String laneId;
    private final long timestamp;

    /**
     * Protected constructor initializing base vehicle attributes.
     *
     * @param id unique identifier of the vehicle
     * @param plateNumber vehicle license plate registration
     * @param type general vehicle type (Sedan, Bus, Ambulance, etc.)
     */
    protected Vehicle(String id, String plateNumber, String type) {
        this.id = Objects.requireNonNull(id, "Vehicle ID cannot be null");
        this.plateNumber = Objects.requireNonNull(plateNumber, "Plate number cannot be null");
        this.type = Objects.requireNonNull(type, "Vehicle type cannot be null");
        this.speed = 0.0;
        this.laneId = "LANE-DEFAULT";
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Calculates the operational priority score of this vehicle (higher value indicates higher priority).
     *
     * @return priority integer score
     */
    public abstract int calculatePriority();

    /**
     * Determines the domain categorization of this vehicle.
     *
     * @return category description (e.g., "COMMERCIAL", "PRIVATE", "EMERGENCY_SERVICES")
     */
    public abstract String getVehicleCategory();

    /**
     * Template Method pattern: defines the skeleton of how a vehicle describes itself,
     * delegating specific category and priority details to concrete subclasses.
     *
     * @return structured textual description of the vehicle
     */
    public final String describe() {
        return String.format("[%s] ID=%s, Plate=%s, Category=%s, Priority=%d, Speed=%.1f km/h, Lane=%s",
                getType(), getId(), getPlateNumber(), getVehicleCategory(), calculatePriority(), getSpeed(), getLaneId());
    }

    @Override
    public int compareTo(Vehicle other) {
        if (other == null) return 1;
        // Primary sort: descending by priority (higher priority first)
        int priorityComparison = Integer.compare(other.calculatePriority(), this.calculatePriority());
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        // Secondary sort: ascending by arrival timestamp (earlier arrivals first)
        return Long.compare(this.timestamp, other.timestamp);
    }

    public String getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public String getType() { return type; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public String getLaneId() { return laneId; }
    public void setLaneId(String laneId) { this.laneId = laneId; }
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return Objects.equals(id, vehicle.id) && Objects.equals(plateNumber, vehicle.plateNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plateNumber);
    }

    @Override
    public String toString() {
        return describe();
    }
}
