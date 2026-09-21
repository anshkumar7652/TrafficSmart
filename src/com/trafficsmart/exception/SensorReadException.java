package com.trafficsmart.exception;

import java.io.IOException;

/**
 * Checked exception thrown when an error occurs during sensor data ingestion or I/O streaming.
 * Demonstrates exception wrapping and cause chaining by explicitly accepting an {@link IOException}.
 *
 * @author Ansh
 * @version 1.0
 */
public class SensorReadException extends TrafficSystemException {

    private static final long serialVersionUID = 1L;
    private final String sensorId;

    /**
     * Constructs a SensorReadException with message, sensor identifier, and root cause.
     *
     * @param message explanatory error description
     * @param sensorId identifier of the sensor where reading failed
     * @param cause underlying I/O exception
     */
    public SensorReadException(String message, String sensorId, IOException cause) {
        super("ERR_SENSOR_IO", message + " (Sensor ID: " + sensorId + ")", cause);
        this.sensorId = sensorId;
    }

    /**
     * Constructs a SensorReadException without root cause.
     *
     * @param message explanatory error description
     * @param sensorId identifier of the sensor
     */
    public SensorReadException(String message, String sensorId) {
        super("ERR_SENSOR_DATA", message + " (Sensor ID: " + sensorId + ")");
        this.sensorId = sensorId;
    }

    /**
     * Returns the sensor identifier associated with this failure.
     *
     * @return sensorId string
     */
    public String getSensorId() {
        return sensorId;
    }
}
