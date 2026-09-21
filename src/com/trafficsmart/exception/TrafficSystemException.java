package com.trafficsmart.exception;

/**
 * Base checked exception for the Smart Traffic Monitoring and Management System.
 * Represents recoverable operational errors within the system domain.
 * Demonstrates checked exception architecture, constructor overloading, and cause chaining.
 *
 * @author Ansh
 * @version 1.0
 */
public class TrafficSystemException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String errorCode;

    /**
     * Constructs a new TrafficSystemException with a detailed message and default error code.
     *
     * @param message the descriptive error message
     */
    public TrafficSystemException(String message) {
        super(message);
        this.errorCode = "ERR_GENERIC_TRAFFIC";
    }

    /**
     * Constructs a new TrafficSystemException with a message and a specific error code.
     *
     * @param errorCode the system error code
     * @param message the descriptive error message
     */
    public TrafficSystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new TrafficSystemException with a message, error code, and underlying cause.
     *
     * @param errorCode the system error code
     * @param message the descriptive error message
     * @param cause the underlying cause for exception chaining
     */
    public TrafficSystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new TrafficSystemException with a message and cause using default error code.
     *
     * @param message the descriptive error message
     * @param cause the underlying cause
     */
    public TrafficSystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ERR_CHAINED_TRAFFIC";
    }

    /**
     * Retrieves the specific error code associated with this exception.
     *
     * @return the error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
}
