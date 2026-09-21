package com.trafficsmart.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable telemetry payload captured from roadside inductive loop or camera sensors.
 * Implements {@link Serializable} for binary log persistence and network transmission.
 *
 * @author Ansh
 * @version 1.0
 */
public class SensorData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String sensorId;
    private final String intersectionId;
    private final int vehicleCount;
    private final double avgSpeed;
    private final long timestamp;

    /**
     * Constructs a new SensorData telemetry record.
     *
     * @param sensorId sensor identifier
     * @param intersectionId intersection monitored
     * @param vehicleCount number of vehicles detected in the interval
     * @param avgSpeed average speed observed in km/h
     */
    public SensorData(String sensorId, String intersectionId, int vehicleCount, double avgSpeed) {
        this.sensorId = Objects.requireNonNull(sensorId, "Sensor ID cannot be null");
        this.intersectionId = Objects.requireNonNull(intersectionId, "Intersection ID cannot be null");
        this.vehicleCount = Math.max(0, vehicleCount);
        this.avgSpeed = Math.max(0.0, avgSpeed);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Overloaded constructor accepting an explicit timestamp (useful for log replay and testing).
     *
     * @param sensorId sensor identifier
     * @param intersectionId intersection identifier
     * @param vehicleCount vehicle count
     * @param avgSpeed average speed
     * @param timestamp epoch timestamp in ms
     */
    public SensorData(String sensorId, String intersectionId, int vehicleCount, double avgSpeed, long timestamp) {
        this.sensorId = Objects.requireNonNull(sensorId, "Sensor ID cannot be null");
        this.intersectionId = Objects.requireNonNull(intersectionId, "Intersection ID cannot be null");
        this.vehicleCount = Math.max(0, vehicleCount);
        this.avgSpeed = Math.max(0.0, avgSpeed);
        this.timestamp = timestamp;
    }

    public String getSensorId() { return sensorId; }
    public String getIntersectionId() { return intersectionId; }
    public int getVehicleCount() { return vehicleCount; }
    public double getAvgSpeed() { return avgSpeed; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("SensorData[ID=%s, Int=%s, Count=%d, AvgSpeed=%.1f km/h, Time=%d]",
                sensorId, intersectionId, vehicleCount, avgSpeed, timestamp);
    }
}
