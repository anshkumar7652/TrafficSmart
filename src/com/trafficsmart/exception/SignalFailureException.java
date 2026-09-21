package com.trafficsmart.exception;

/**
 * Unchecked runtime exception thrown when an invalid traffic signal state transition or hardware fault occurs.
 * Represents programming bugs, corrupted hardware telemetry, or unrecoverable state inconsistencies.
 *
 * @author Ansh
 * @version 1.0
 */
public class SignalFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String signalId;

    /**
     * Constructs a SignalFailureException with an informative message and signal identifier.
     *
     * @param message descriptive failure message
     * @param signalId identifier of the failed traffic signal
     */
    public SignalFailureException(String message, String signalId) {
        super("Signal Fault [" + signalId + "]: " + message);
        this.signalId = signalId;
    }

    /**
     * Constructs a SignalFailureException with a message, signal ID, and root cause.
     *
     * @param message descriptive failure message
     * @param signalId identifier of the signal
     * @param cause underlying cause
     */
    public SignalFailureException(String message, String signalId, Throwable cause) {
        super("Signal Fault [" + signalId + "]: " + message, cause);
        this.signalId = signalId;
    }

    /**
     * Returns the signal identifier that encountered the fault.
     *
     * @return signalId string
     */
    public String getSignalId() {
        return signalId;
    }
}
