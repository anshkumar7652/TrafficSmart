package com.trafficsmart.model;

/**
 * Enumeration representing the valid operational states of a traffic signal.
 * Implements duration state and provides automatic cycle transitions.
 *
 * @author Ansh
 * @version 1.0
 */
public enum SignalState {
    RED(30, "STOP"),
    YELLOW(5, "CAUTION"),
    GREEN(25, "PROCEED");

    private final int defaultDurationSeconds;
    private final String description;

    SignalState(int defaultDurationSeconds, String description) {
        this.defaultDurationSeconds = defaultDurationSeconds;
        this.description = description;
    }

    /**
     * Gets the default duration in seconds for this signal state.
     *
     * @return duration in seconds
     */
    public int getDefaultDurationSeconds() {
        return defaultDurationSeconds;
    }

    /**
     * Gets a human-readable operational description.
     *
     * @return description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Computes the next natural state in the traffic signal cycle sequence:
     * GREEN -> YELLOW -> RED -> GREEN.
     *
     * @return the next SignalState enum value
     */
    public SignalState next() {
        return switch (this) {
            case GREEN -> YELLOW;
            case YELLOW -> RED;
            case RED -> GREEN;
        };
    }
}
