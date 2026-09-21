package com.trafficsmart.service;

import com.trafficsmart.config.SystemConfig;
import com.trafficsmart.exception.SensorReadException;
import com.trafficsmart.model.EmergencyVehicle;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;
import com.trafficsmart.model.StandardVehicle;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.model.Vehicle;
import com.trafficsmart.repository.GenericRepository;
import com.trafficsmart.repository.VehicleRepository;
import com.trafficsmart.util.FileIOHandler;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Background simulation engine driving real-time traffic dynamics.
 * Demonstrates Unit 4 requirements:
 * 1. Implements {@link Runnable}
 * 2. Runs as a daemon background thread
 * 3. Uses {@code volatile} control flags
 * 4. Protects shared state using {@link ReentrantLock}
 * 5. Handles {@link Thread#sleep} timing.
 *
 * @author Ansh
 * @version 1.0
 */
public class SimulationEngine implements Runnable {

    private volatile boolean running;
    private final Lock stateLock;
    private final GenericRepository<Intersection> intersectionRepo;
    private final VehicleRepository vehicleRepo;
    private final Random random;
    private long vehicleCounter;

    /**
     * Constructs the simulation engine with required repository references.
     *
     * @param intersectionRepo repository holding intersections
     * @param vehicleRepo repository managing vehicles
     */
    public SimulationEngine(GenericRepository<Intersection> intersectionRepo, VehicleRepository vehicleRepo) {
        this.intersectionRepo = intersectionRepo;
        this.vehicleRepo = vehicleRepo;
        this.stateLock = new ReentrantLock();
        this.random = new Random();
        this.running = false;
        this.vehicleCounter = 1000;
    }

    @Override
    public void run() {
        System.out.println("[SIMULATION] Background simulation engine started.");
        SystemConfig config = SystemConfig.getInstance();

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Execute a simulation tick
                tickSimulation();

                // Sleep for configured tick interval (default 1000ms)
                Thread.sleep(config.getSimulationTickMs());
            } catch (InterruptedException e) {
                System.out.println("[SIMULATION] Simulation engine interrupted.");
                break;
            }
        }
        System.out.println("[SIMULATION] Background simulation engine halted.");
    }

    /**
     * Executes one discrete simulation step.
     * Uses ReentrantLock to prevent data corruption with UI and network read threads.
     */
    public void tickSimulation() {
        stateLock.lock();
        try {
            List<Intersection> intersections = intersectionRepo.findAll();
            for (Intersection inter : intersections) {
                // 1. Simulate vehicle arrivals across approaches
                for (int approach = 0; approach < 4; approach++) {
                    int lanes = inter.getLaneDensityMatrix()[approach].length;
                    for (int lane = 0; lane < lanes; lane++) {
                        // Fluctuate count by -2 to +3 vehicles
                        int delta = random.nextInt(6) - 2;
                        int currentCount = inter.getLaneDensityMatrix()[approach][lane];
                        int newCount = Math.max(0, Math.min(18, currentCount + delta));
                        inter.setLaneDensity(approach, lane, newCount);
                    }
                }

                // 2. Occasionally spawn an individual tracked vehicle
                if (random.nextDouble() < 0.35) {
                    spawnRandomVehicle(inter);
                }

                // 3. Adapt signal durations based on current density
                for (TrafficSignal signal : inter.getSignals()) {
                    SensorData mockData = new SensorData(
                            "SNS-" + inter.getIntersectionId() + "-" + signal.getApproachDirection(),
                            inter.getIntersectionId(),
                            inter.getTotalVehicles(),
                            35.0 - (inter.getCongestionLevel() * 0.25)
                    );

                    signal.adaptDuration(mockData, inter);

                    // Write binary telemetry log (Unit 4 byte stream demo)
                    try {
                        FileIOHandler.appendSensorDataBinary(mockData, "data/logs/sensor_live.dat");
                    } catch (SensorReadException ignored) {
                        // Non-fatal telemetry log error
                    }
                }
            }

        } finally {
            stateLock.unlock();
        }
    }

    private void spawnRandomVehicle(Intersection inter) {
        vehicleCounter++;
        String id = "VH-" + vehicleCounter;
        String[] prefixes = {"UP16", "DL01", "HR26", "MH02"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String plate = prefix + (char)('A' + random.nextInt(26)) + (1000 + random.nextInt(9000));

        Vehicle v;
        if (random.nextDouble() < 0.10) {
            // Spawn Emergency Vehicle
            String[] emergencyTypes = {"AMBULANCE", "FIRE_ENGINE", "POLICE"};
            String type = emergencyTypes[random.nextInt(emergencyTypes.length)];
            v = new EmergencyVehicle(id, plate, type, 9);
            v.setSpeed(65.0 + random.nextDouble() * 20.0);
        } else {
            // Spawn Standard Vehicle
            String[] types = {"Sedan", "SUV", "Bus", "Truck"};
            String[] fuels = {"PETROL", "DIESEL", "ELECTRIC", "HYBRID"};
            String type = types[random.nextInt(types.length)];
            String fuel = fuels[random.nextInt(fuels.length)];
            v = new StandardVehicle(id, plate, type, fuel);
            v.setSpeed(25.0 + random.nextDouble() * 30.0);
        }

        v.setLaneId("LANE-" + inter.getIntersectionId() + "-" + random.nextInt(4));
        vehicleRepo.save(v.getId(), v);

        // Keep repository bounded so memory remains lean
        if (vehicleRepo.count() > 50) {
            List<Vehicle> all = vehicleRepo.findAll();
            if (!all.isEmpty()) {
                vehicleRepo.deleteById(all.get(0).getId());
            }
        }
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
