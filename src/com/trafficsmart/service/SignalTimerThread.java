package com.trafficsmart.service;

import com.trafficsmart.model.TrafficSignal;

/**
 * Dedicated thread managing the real-time countdown timer and phase transitions for an individual TrafficSignal.
 * Demonstrates Unit 4 requirement: Subclassing {@link Thread}, thread lifecycle management,
 * {@code volatile} termination flag, and handling {@link InterruptedException}.
 *
 * @author Ansh
 * @version 1.0
 */
public class SignalTimerThread extends Thread {

    private final TrafficSignal signal;
    private volatile boolean active;
    private int remainingSeconds;

    /**
     * Constructs a SignalTimerThread for a specific TrafficSignal.
     *
     * @param signal target traffic signal to control
     */
    public SignalTimerThread(TrafficSignal signal) {
        super("TimerThread-" + signal.getSignalId());
        this.signal = signal;
        this.active = true;
        this.remainingSeconds = signal.getCurrentDurationSeconds();
    }

    @Override
    public void run() {
        System.out.println("[THREAD] Started timer loop for " + getName());
        while (active && !isInterrupted()) {
            try {
                Thread.sleep(1000); // 1 second countdown tick

                synchronized (this) {
                    remainingSeconds--;
                    if (remainingSeconds <= 0) {
                        // Cycle signal to the next state
                        signal.cycleNext();
                        remainingSeconds = signal.getCurrentDurationSeconds();
                    }
                }

            } catch (InterruptedException e) {
                // Gracefully catch interruption to re-evaluate active flag or reset
                if (!active) {
                    break;
                }
            }
        }
        System.out.println("[THREAD] Stopped timer loop for " + getName());
    }

    /**
     * Deactivates the timer thread and sends an interrupt signal to exit sleep immediately.
     */
    public void deactivate() {
        this.active = false;
        this.interrupt();
    }

    /**
     * Resets countdown to a new duration value (e.g. during emergency override).
     *
     * @param newSeconds new remaining seconds
     */
    public synchronized void resetCountdown(int newSeconds) {
        this.remainingSeconds = Math.max(1, newSeconds);
        this.interrupt(); // Wake up thread to immediately register new duration
    }

    public synchronized int getRemainingSeconds() {
        return remainingSeconds;
    }
}
