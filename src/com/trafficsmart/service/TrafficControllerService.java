package com.trafficsmart.service;

import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SignalState;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.repository.GenericRepository;
import com.trafficsmart.repository.VehicleRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Core business orchestrator coordinating repositories, simulation engine, signal timer threads, and GUI observers.
 * Demonstrates Unit 5 {@link LinkedList} queue usage and multi-threaded coordination.
 *
 * @author Ansh
 * @version 1.0
 */
public final class TrafficControllerService {

    private final GenericRepository<Intersection> intersectionRepo;
    private final VehicleRepository vehicleRepo;
    private final SimulationEngine simulationEngine;
    private final ReportGenerator reportGenerator;
    private final List<SignalTimerThread> timerThreads;
    private final LinkedList<String> eventQueue; // Unit 5 requirement: LinkedList FIFO event queue
    private final List<Consumer<String>> systemObservers;
    private Thread simulationThread;

    /**
     * Constructs the TrafficControllerService and bootstraps standard default intersections.
     */
    public TrafficControllerService() {
        this.intersectionRepo = new GenericRepository<>();
        this.vehicleRepo = new VehicleRepository();
        this.simulationEngine = new SimulationEngine(intersectionRepo, vehicleRepo);
        this.reportGenerator = new ReportGenerator();
        this.timerThreads = new ArrayList<>();
        this.eventQueue = new LinkedList<>();
        this.systemObservers = new ArrayList<>();

        bootstrapDefaultNetwork();
    }

    /**
     * Populates standard default intersections and signals for the metropolitan grid.
     */
    public final void bootstrapDefaultNetwork() {
        // Intersection 1: Central Square
        Intersection int1 = new Intersection("INT-001", "Knowledge Park Metro Cross", "Sector Alpha");
        TrafficSignal sig1N = new TrafficSignal("SIG-101", "INT-001", "NORTH");
        TrafficSignal sig1S = new TrafficSignal("SIG-102", "INT-001", "SOUTH");
        TrafficSignal sig1E = new TrafficSignal("SIG-103", "INT-001", "EAST");
        TrafficSignal sig1W = new TrafficSignal("SIG-104", "INT-001", "WEST");

        // Initial phase staggered
        sig1N.setState(SignalState.GREEN, 25);
        sig1S.setState(SignalState.GREEN, 25);
        sig1E.setState(SignalState.RED, 30);
        sig1W.setState(SignalState.RED, 30);

        int1.addSignal(sig1N);
        int1.addSignal(sig1S);
        int1.addSignal(sig1E);
        int1.addSignal(sig1W);
        intersectionRepo.save(int1.getIntersectionId(), int1);

        // Intersection 2: Express Highway Junction
        Intersection int2 = new Intersection("INT-002", "Pari Chowk Junction", "Sector Beta");
        TrafficSignal sig2N = new TrafficSignal("SIG-201", "INT-002", "NORTH");
        TrafficSignal sig2S = new TrafficSignal("SIG-202", "INT-002", "SOUTH");
        TrafficSignal sig2E = new TrafficSignal("SIG-203", "INT-002", "EAST");
        TrafficSignal sig2W = new TrafficSignal("SIG-204", "INT-002", "WEST");

        sig2N.setState(SignalState.RED, 30);
        sig2S.setState(SignalState.RED, 30);
        sig2E.setState(SignalState.GREEN, 25);
        sig2W.setState(SignalState.GREEN, 25);

        int2.addSignal(sig2N);
        int2.addSignal(sig2S);
        int2.addSignal(sig2E);
        int2.addSignal(sig2W);
        intersectionRepo.save(int2.getIntersectionId(), int2);

        // Intersection 3: Tech Boulevard
        Intersection int3 = new Intersection("INT-003", "IT Expressway Terminal", "Sector Gamma");
        TrafficSignal sig3N = new TrafficSignal("SIG-301", "INT-003", "NORTH");
        TrafficSignal sig3S = new TrafficSignal("SIG-302", "INT-003", "SOUTH");
        TrafficSignal sig3E = new TrafficSignal("SIG-303", "INT-003", "EAST");
        TrafficSignal sig3W = new TrafficSignal("SIG-304", "INT-003", "WEST");

        int3.addSignal(sig3N);
        int3.addSignal(sig3S);
        int3.addSignal(sig3E);
        int3.addSignal(sig3W);
        intersectionRepo.save(int3.getIntersectionId(), int3);

        addEvent("System initialized with 3 intersections and 12 traffic signals.");
    }

    /**
     * Starts simulation and timer background threads.
     */
    public synchronized void startSystem() {
        // Start simulation thread as a daemon thread
        if (simulationThread == null || !simulationThread.isAlive()) {
            simulationEngine.start();
            simulationThread = new Thread(simulationEngine, "TrafficSimulationDaemon");
            simulationThread.setDaemon(true); // Daemon thread requirement
            simulationThread.start();
        }

        // Start timer threads for each signal
        for (Intersection inter : intersectionRepo.findAll()) {
            for (TrafficSignal sig : inter.getSignals()) {
                SignalTimerThread timer = new SignalTimerThread(sig);
                timer.setDaemon(true);
                timer.start();
                timerThreads.add(timer);
            }
        }

        addEvent("Traffic controller system threads started successfully.");
    }

    /**
     * Gracefully stops all active background worker and timer threads.
     */
    public synchronized void shutdownSystem() {
        simulationEngine.stop();
        if (simulationThread != null) {
            simulationThread.interrupt();
        }

        for (SignalTimerThread timer : timerThreads) {
            timer.deactivate();
        }
        timerThreads.clear();
        addEvent("Traffic controller system threads stopped.");
    }

    /**
     * Handles high-priority emergency vehicle clearance override at an intersection.
     *
     * @param intersectionId target intersection
     * @param vehicleId vehicle triggering the override
     */
    public synchronized void handleEmergencyOverride(String intersectionId, String vehicleId) {
        Intersection inter = intersectionRepo.findById(intersectionId);
        if (inter == null) {
            addEvent("Emergency override failed: intersection " + intersectionId + " not found.");
            return;
        }

        // Override all signals at intersection: North/South or appropriate corridor to GREEN, cross traffic to RED
        for (TrafficSignal sig : inter.getSignals()) {
            if ("NORTH".equalsIgnoreCase(sig.getApproachDirection()) || "SOUTH".equalsIgnoreCase(sig.getApproachDirection())) {
                sig.overrideToGreen();
            } else {
                sig.setState(SignalState.RED, 35);
            }
        }

        String alert = String.format("EMERGENCY ALERT: Priority corridor granted for %s at %s!", vehicleId, inter.getName());
        addEvent(alert);
        notifyObservers("EMERGENCY:" + intersectionId);
    }

    /**
     * Manually overrides a specific traffic signal state.
     *
     * @param intersectionId intersection identifier
     * @param signalId signal identifier
     * @param targetState desired SignalState
     * @throws TrafficSystemException if entity not found
     */
    public synchronized void setManualSignalState(String intersectionId, String signalId, SignalState targetState)
            throws TrafficSystemException {
        Intersection inter = intersectionRepo.findById(intersectionId);
        if (inter == null) {
            throw new TrafficSystemException("ERR_NOT_FOUND", "Intersection not found: " + intersectionId);
        }

        TrafficSignal targetSig = null;
        for (TrafficSignal sig : inter.getSignals()) {
            if (sig.getSignalId().equalsIgnoreCase(signalId)) {
                targetSig = sig;
                break;
            }
        }

        if (targetSig == null) {
            throw new TrafficSystemException("ERR_NOT_FOUND", "Signal not found: " + signalId);
        }

        targetSig.setState(targetState, targetState.getDefaultDurationSeconds());
        addEvent(String.format("Manual override: Signal %s at %s set to %s", signalId, intersectionId, targetState));
        notifyObservers("UPDATE:" + intersectionId);
    }

    /**
     * Adds an entry to the thread-safe LinkedList event queue.
     *
     * @param message log string
     */
    public synchronized void addEvent(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formatted = String.format("[%s] %s", sdf.format(new Date()), message);

        eventQueue.addFirst(formatted);
        // Retain max 100 recent entries in queue
        while (eventQueue.size() > 100) {
            eventQueue.removeLast();
        }
    }

    /**
     * Returns a snapshot of recent events from the FIFO queue.
     *
     * @param maxCount maximum number of events to return
     * @return list of event strings
     */
    public synchronized List<String> getRecentEvents(int maxCount) {
        int count = Math.min(maxCount, eventQueue.size());
        List<String> result = new ArrayList<>(count);
        int i = 0;
        for (String event : eventQueue) {
            if (i++ >= count) break;
            result.add(event);
        }
        return result;
    }

    /**
     * Registers an observer callback for real-time notification dispatching.
     *
     * @param observer Consumer callback
     */
    public synchronized void registerObserver(Consumer<String> observer) {
        if (observer != null && !systemObservers.contains(observer)) {
            systemObservers.add(observer);
        }
    }

    private synchronized void notifyObservers(String notification) {
        for (Consumer<String> obs : systemObservers) {
            obs.accept(notification);
        }
    }

    public String generateSystemReport() {
        return reportGenerator.generateFullReport(intersectionRepo.findAll(), vehicleRepo.findAll());
    }

    public GenericRepository<Intersection> getIntersectionRepo() { return intersectionRepo; }
    public VehicleRepository getVehicleRepo() { return vehicleRepo; }
    public SimulationEngine getSimulationEngine() { return simulationEngine; }
    public ReportGenerator getReportGenerator() { return reportGenerator; }
}
