package com.trafficsmart.repository;

import com.trafficsmart.model.EmergencyVehicle;
import com.trafficsmart.model.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Specialized domain repository for managing Vehicle entities.
 * Extends {@link GenericRepository} and adds vehicle-specific query methods.
 * Demonstrates inheritance with generic type specialization.
 *
 * @author Ansh
 * @version 1.0
 */
public class VehicleRepository extends GenericRepository<Vehicle> {

    /**
     * Finds a vehicle by its registration plate number.
     *
     * @param plate license plate string
     * @return matching Vehicle or null
     */
    public synchronized Vehicle findByPlate(String plate) {
        if (plate == null || plate.isBlank()) {
            return null;
        }
        List<Vehicle> matches = filterBy(v -> v.getPlateNumber().equalsIgnoreCase(plate.trim()));
        return matches.isEmpty() ? null : matches.get(0);
    }

    /**
     * Retrieves all emergency vehicles currently tracked in the system.
     *
     * @return list of EmergencyVehicle instances
     */
    public synchronized List<EmergencyVehicle> findEmergencyVehicles() {
        List<EmergencyVehicle> emergencyList = new ArrayList<>();
        for (Vehicle v : findAll()) {
            if (v instanceof EmergencyVehicle ev) {
                emergencyList.add(ev);
            }
        }
        return emergencyList;
    }

    /**
     * Finds all vehicles registered in a given lane approach.
     *
     * @param laneId lane identifier
     * @return list of vehicles in the lane
     */
    public synchronized List<Vehicle> findVehiclesInLane(String laneId) {
        return filterBy(v -> laneId != null && laneId.equalsIgnoreCase(v.getLaneId()));
    }

    /**
     * Returns all vehicles sorted in descending order of current speed.
     *
     * @return speed-sorted list of vehicles
     */
    public synchronized List<Vehicle> getVehiclesSortedBySpeed() {
        return getSorted(Comparator.comparingDouble(Vehicle::getSpeed).reversed());
    }
}
