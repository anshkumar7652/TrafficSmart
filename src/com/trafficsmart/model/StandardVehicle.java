package com.trafficsmart.model;

/**
 * Represents a standard civilian or commercial vehicle (e.g. Car, SUV, Truck, Electric Scooter).
 * Extends {@link Vehicle} and implements subclass-specific behaviors.
 *
 * @author Ansh
 * @version 1.0
 */
public class StandardVehicle extends Vehicle {

    private static final long serialVersionUID = 1L;
    private final String fuelType; // PETROL, DIESEL, ELECTRIC, HYBRID

    /**
     * Constructs a StandardVehicle with basic attributes and fuel categorization.
     *
     * @param id unique ID
     * @param plateNumber license plate
     * @param type vehicle type description
     * @param fuelType engine propulsion fuel type
     */
    public StandardVehicle(String id, String plateNumber, String type, String fuelType) {
        super(id, plateNumber, type);
        this.fuelType = (fuelType != null && !fuelType.isBlank()) ? fuelType.toUpperCase() : "PETROL";
    }

    @Override
    public int calculatePriority() {
        // Standard vehicles have normal operational priority (1 to 3)
        // Public transit/buses get slightly higher weight
        if ("Bus".equalsIgnoreCase(getType())) {
            return 3;
        } else if ("Truck".equalsIgnoreCase(getType())) {
            return 2;
        }
        return 1;
    }

    @Override
    public String getVehicleCategory() {
        return "STANDARD_" + fuelType;
    }

    public String getFuelType() {
        return fuelType;
    }
}
