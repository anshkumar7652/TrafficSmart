package com.trafficsmart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a major roadway intersection managed by the smart traffic system.
 * Implements {@link Monitorable} and {@link Comparable} for sorting by congestion level.
 * Features a jagged 2D array for multi-lane density representation (Unit 2 requirement).
 *
 * @author Ansh
 * @version 1.0
 */
public class Intersection implements Monitorable, Comparable<Intersection> {

    private final String intersectionId;
    private final String name;
    private final String location;
    private final List<TrafficSignal> signals;

    /**
     * Jagged 2D array modeling variable lane densities across 4 approaches:
     * Row 0: North approach lanes (e.g. 3 lanes: left, straight, right)
     * Row 1: South approach lanes (e.g. 3 lanes)
     * Row 2: East approach lanes  (e.g. 2 lanes)
     * Row 3: West approach lanes  (e.g. 2 lanes)
     */
    private int[][] laneDensityMatrix;
    private double congestionLevel; // Percentage 0.0 to 100.0
    private long lastUpdated;

    /**
     * Constructs a new Intersection entity with empty signals and a default jagged lane structure.
     *
     * @param intersectionId unique identifier (e.g. "INT-001")
     * @param name human-friendly title (e.g. "Main St & 5th Ave")
     * @param location geographical or sector identifier
     */
    public Intersection(String intersectionId, String name, String location) {
        this.intersectionId = Objects.requireNonNull(intersectionId, "Intersection ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        this.signals = new ArrayList<>();
        this.lastUpdated = System.currentTimeMillis();

        // Initialize default jagged array: North (3 lanes), South (3 lanes), East (2 lanes), West (2 lanes)
        this.laneDensityMatrix = new int[][] {
            new int[3], // North approach
            new int[3], // South approach
            new int[2], // East approach
            new int[2]  // West approach
        };
        this.congestionLevel = 0.0;
    }

    /**
     * Adds a traffic signal to this intersection.
     *
     * @param signal TrafficSignal instance
     */
    public synchronized void addSignal(TrafficSignal signal) {
        if (signal != null && !signals.contains(signal)) {
            signals.add(signal);
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    /**
     * Updates a specific lane's vehicle count within the jagged density array.
     *
     * @param approachIndex 0=North, 1=South, 2=East, 3=West
     * @param laneIndex specific lane within that approach
     * @param count count of vehicles
     */
    public synchronized void setLaneDensity(int approachIndex, int laneIndex, int count) {
        if (approachIndex >= 0 && approachIndex < laneDensityMatrix.length) {
            if (laneIndex >= 0 && laneIndex < laneDensityMatrix[approachIndex].length) {
                laneDensityMatrix[approachIndex][laneIndex] = Math.max(0, count);
                recalculateCongestion();
                this.lastUpdated = System.currentTimeMillis();
            }
        }
    }

    /**
     * Recalculates the overall congestion index across all lanes in the jagged array.
     */
    public synchronized void recalculateCongestion() {
        int totalVehicles = 0;
        int totalLanes = 0;

        // Process jagged array using nested loops
        for (int i = 0; i < laneDensityMatrix.length; i++) {
            for (int j = 0; j < laneDensityMatrix[i].length; j++) {
                totalVehicles += laneDensityMatrix[i][j];
                totalLanes++;
            }
        }

        // Assume maximum nominal capacity is 15 vehicles per lane
        int maxCapacity = Math.max(1, totalLanes * 15);
        this.congestionLevel = Math.min(100.0, (totalVehicles * 100.0) / maxCapacity);
    }

    public synchronized int getTotalVehicles() {
        int sum = 0;
        for (int[] approach : laneDensityMatrix) {
            for (int count : approach) {
                sum += count;
            }
        }
        return sum;
    }

    public String getIntersectionId() { return intersectionId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public synchronized List<TrafficSignal> getSignals() { return Collections.unmodifiableList(signals); }
    public synchronized int[][] getLaneDensityMatrix() { return laneDensityMatrix; }
    public synchronized void setLaneDensityMatrix(int[][] matrix) {
        this.laneDensityMatrix = matrix;
        recalculateCongestion();
    }
    public synchronized double getCongestionLevel() { return congestionLevel; }

    @Override
    public int compareTo(Intersection other) {
        if (other == null) return 1;
        // Primary sort: descending by congestion level (highest congestion first)
        int comp = Double.compare(other.getCongestionLevel(), this.getCongestionLevel());
        if (comp != 0) {
            return comp;
        }
        return this.intersectionId.compareTo(other.intersectionId);
    }

    @Override
    public synchronized String getStatus() {
        return String.format("[%s: %s] Congestion=%.1f%%, TotalVehicles=%d, Signals=%d",
                intersectionId, name, congestionLevel, getTotalVehicles(), signals.size());
    }

    @Override
    public synchronized Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("intersectionId", intersectionId);
        metrics.put("name", name);
        metrics.put("location", location);
        metrics.put("congestionLevel", congestionLevel);
        metrics.put("totalVehicles", getTotalVehicles());
        metrics.put("signalCount", signals.size());
        metrics.put("lastUpdated", lastUpdated);
        return metrics;
    }

    @Override
    public synchronized long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Intersection that)) return false;
        return Objects.equals(intersectionId, that.intersectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intersectionId);
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
