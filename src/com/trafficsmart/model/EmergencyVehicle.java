package com.trafficsmart.model;

/**
 * Represents an emergency vehicle (Ambulance, Fire Engine, Police Cruiser).
 * Carries elevated priority scores and can trigger immediate signal overrides.
 *
 * @author Ansh
 * @version 1.0
 */
public class EmergencyVehicle extends Vehicle {

    private static final long serialVersionUID = 1L;

    private final String emergencyType; // AMBULANCE, FIRE_ENGINE, POLICE
    private boolean sirenActive;
    private final int basePriority;

    /**
     * Constructs an EmergencyVehicle with specific priority rating.
     *
     * @param id unique ID
     * @param plateNumber license plate
     * @param emergencyType department designation
     * @param basePriority priority tier (typically 8 to 10)
     */
    public EmergencyVehicle(String id, String plateNumber, String emergencyType, int basePriority) {
        super(id, plateNumber, "Emergency-" + emergencyType);
        this.emergencyType = emergencyType.toUpperCase();
        this.basePriority = Math.max(5, basePriority);
        this.sirenActive = true;
    }

    @Override
    public int calculatePriority() {
        // Siren active gives maximum override priority
        return sirenActive ? (basePriority + 5) : basePriority;
    }

    @Override
    public String getVehicleCategory() {
        return "EMERGENCY_RESPONSE";
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public boolean isSirenActive() {
        return sirenActive;
    }

    public void setSirenActive(boolean sirenActive) {
        this.sirenActive = sirenActive;
    }

    public int getBasePriority() {
        return basePriority;
    }
}
