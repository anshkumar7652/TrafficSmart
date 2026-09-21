package com.trafficsmart.service;

import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.model.Vehicle;
import com.trafficsmart.util.FileIOHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service dedicated to data formatting, analytical report synthesis, and regex validation.
 * Demonstrates Unit 3 requirements:
 * 1. Efficient text processing using {@link StringBuilder} and {@link String#format}
 * 2. Regular Expressions with {@link Pattern} and {@link Matcher} for plate validation
 * 3. Export integration with Character Streams.
 *
 * @author Ansh
 * @version 1.0
 */
public class ReportGenerator {

    // Regex pattern: 2 capital letters (state) + 2 digits + 1-2 letters + 4 digits (e.g. UP16AB1234 or DL01C4567)
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$");

    /**
     * Validates vehicle registration license plates using Regular Expressions.
     *
     * @param plate license plate string
     * @return true if matches standard municipal format
     */
    public boolean validatePlateNumber(String plate) {
        if (plate == null) return false;
        Matcher matcher = PLATE_PATTERN.matcher(plate.trim().toUpperCase());
        return matcher.matches();
    }

    /**
     * Generates an extensive, human-readable traffic operational report.
     * Built entirely using {@link StringBuilder} for optimal string handling.
     *
     * @param intersections list of active intersections
     * @param vehicles fleet of tracked vehicles
     * @return complete formatted report text
     */
    public String generateFullReport(List<Intersection> intersections, List<Vehicle> vehicles) {
        StringBuilder sb = new StringBuilder(1024);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        sb.append("================================================================================\n");
        sb.append("               SMART TRAFFIC MONITORING & MANAGEMENT SYSTEM REPORT               \n");
        sb.append(String.format("               Generated: %s | Active Nodes: %d                \n",
                timestamp, (intersections != null ? intersections.size() : 0)));
        sb.append("================================================================================\n\n");

        sb.append("--- [1] INTERSECTION OPERATIONAL STATUS ---\n");
        if (intersections == null || intersections.isEmpty()) {
            sb.append("No active intersections registered.\n");
        } else {
            for (Intersection inter : intersections) {
                sb.append(String.format("Node: %-8s | Name: %-25s | Congestion: %5.1f%% | Vehicles: %3d\n",
                        inter.getIntersectionId(), inter.getName(), inter.getCongestionLevel(), inter.getTotalVehicles()));

                // Signal details for this node
                for (TrafficSignal sig : inter.getSignals()) {
                    sb.append(String.format("   ↳ Signal [%-6s - %-5s] State: %-6s | Window: %2ds | Override: %b\n",
                            sig.getSignalId(), sig.getApproachDirection(), sig.getCurrentState(),
                            sig.getCurrentDurationSeconds(), sig.isManualOverrideActive()));
                }
            }
        }
        sb.append("\n");

        sb.append("--- [2] VEHICLE FLEET SUMMARY ---\n");
        if (vehicles == null || vehicles.isEmpty()) {
            sb.append("No vehicles currently within monitored sectors.\n");
        } else {
            sb.append(String.format("Total Tracked: %d vehicles\n", vehicles.size()));
            int sampleCount = Math.min(vehicles.size(), 10);
            sb.append(String.format("Displaying latest %d records:\n", sampleCount));
            for (int i = 0; i < sampleCount; i++) {
                Vehicle v = vehicles.get(i);
                boolean isValidPlate = validatePlateNumber(v.getPlateNumber());
                sb.append(String.format("   • %-10s | Plate: %-12s (Valid: %5b) | Priority: %2d | Speed: %5.1f km/h\n",
                        v.getId(), v.getPlateNumber(), isValidPlate, v.calculatePriority(), v.getSpeed()));
            }
        }
        sb.append("\n================================================================================\n");

        return sb.toString();
    }

    /**
     * Exports current intersection telemetry to a CSV file.
     *
     * @param intersections list of intersections
     * @param targetFilePath destination CSV path
     * @throws TrafficSystemException if export fails
     */
    public void exportIntersectionsToCsv(List<Intersection> intersections, String targetFilePath)
            throws TrafficSystemException {
        List<String[]> csvRows = new ArrayList<>();
        csvRows.add(new String[]{"IntersectionID", "Name", "Location", "CongestionPercentage", "TotalVehicles", "SignalCount"});

        if (intersections != null) {
            for (Intersection inter : intersections) {
                csvRows.add(new String[]{
                        inter.getIntersectionId(),
                        inter.getName(),
                        inter.getLocation(),
                        String.format("%.2f", inter.getCongestionLevel()),
                        String.valueOf(inter.getTotalVehicles()),
                        String.valueOf(inter.getSignals().size())
                });
            }
        }
        FileIOHandler.writeCsvReport(csvRows, targetFilePath);
    }
}
