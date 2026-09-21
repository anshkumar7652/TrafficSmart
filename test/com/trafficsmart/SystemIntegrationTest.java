package com.trafficsmart;

import com.trafficsmart.exception.SensorReadException;
import com.trafficsmart.exception.SignalFailureException;
import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.EmergencyVehicle;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;
import com.trafficsmart.model.SignalState;
import com.trafficsmart.model.StandardVehicle;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.model.Vehicle;
import com.trafficsmart.net.EmergencySignalUDP;
import com.trafficsmart.net.MonitoringClient;
import com.trafficsmart.net.TrafficServer;
import com.trafficsmart.repository.GenericRepository;
import com.trafficsmart.repository.VehicleRepository;
import com.trafficsmart.service.ReportGenerator;
import com.trafficsmart.service.TrafficControllerService;
import com.trafficsmart.strategy.DynamicDensityStrategy;
import com.trafficsmart.strategy.SignalControlStrategy;
import com.trafficsmart.util.AnalyticsUtils;
import com.trafficsmart.util.FileIOHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Comprehensive Automated System Test Suite.
 * Validates requirements across all 5 syllabus units without external dependencies.
 *
 * @author Ansh
 * @version 1.0
 */
public class SystemIntegrationTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   RUNNING AUTOMATED TEST SUITE FOR CAPSTONE REQUIREMENTS      ");
        System.out.println("===============================================================");

        testUnit1_OOADAndBasics();
        testUnit2_OOP_Arrays_Lambdas();
        testUnit3_Packages_Exceptions_Strings();
        testUnit4_Multithreading_IO_Sockets();
        testUnit5_Generics_Collections();

        System.out.println("\n===============================================================");
        System.out.printf("   TEST SUMMARY: %d PASSED | %d FAILED                         %n", testsPassed, testsFailed);
        System.out.println("===============================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.printf("[PASS] %s%n", testName);
            testsPassed++;
        } else {
            System.err.printf("[FAIL] %s%n", testName);
            testsFailed++;
        }
    }

    // ==========================================
    // UNIT 1: OOAD & Core Basics
    // ==========================================
    private static void testUnit1_OOADAndBasics() {
        System.out.println("\n--- [UNIT 1] OOAD & Java Basics ---");

        // Object creation, encapsulation, getters
        StandardVehicle car = new StandardVehicle("VH-U1", "UP16AB1234", "Sedan", "ELECTRIC");
        assertTrue("Vehicle getters & encapsulation",
                car.getId().equals("VH-U1") && car.getPlateNumber().equals("UP16AB1234") && car.getFuelType().equals("ELECTRIC"));

        // Template method
        String desc = car.describe();
        assertTrue("Template method describe() output", desc.contains("Category=STANDARD_ELECTRIC") && desc.contains("Priority=1"));
    }

    // ==========================================
    // UNIT 2: OOP Features, Arrays, Lambdas
    // ==========================================
    private static void testUnit2_OOP_Arrays_Lambdas() {
        System.out.println("\n--- [UNIT 2] OOP Features, Arrays & Lambdas ---");

        // Polymorphism (Subclassing & Overriding)
        Vehicle v1 = new StandardVehicle("V-1", "DL01AA1111", "Car", "PETROL");
        Vehicle v2 = new EmergencyVehicle("V-2", "DL01EM9999", "AMBULANCE", 9);

        assertTrue("Polymorphic Priority Standard", v1.calculatePriority() == 1);
        assertTrue("Polymorphic Priority Emergency with Siren", v2.calculatePriority() == 14); // 9 + 5

        // Jagged Array in Intersection
        Intersection inter = new Intersection("INT-TEST", "Test Cross", "Sector 1");
        int[][] jagged = inter.getLaneDensityMatrix();
        assertTrue("Jagged Array rows exist", jagged.length == 4);
        assertTrue("North approach has 3 lanes", jagged[0].length == 3);
        assertTrue("East approach has 2 lanes", jagged[2].length == 2);

        inter.setLaneDensity(0, 0, 10);
        inter.setLaneDensity(0, 1, 5);
        assertTrue("Jagged array congestion recalculation", inter.getTotalVehicles() == 15 && inter.getCongestionLevel() > 0);

        // Functional Interface & Lambdas
        SignalControlStrategy customLambda = (data, context) -> 42;
        int result = customLambda.calculateGreenDuration(null, null);
        assertTrue("Functional Interface Lambda execution", result == 42);

        // Dynamic Density Strategy
        DynamicDensityStrategy strategy = new DynamicDensityStrategy(10, 50);
        SensorData highTraffic = new SensorData("SNS-1", "INT-TEST", 25, 12.0);
        int calcGreen = strategy.calculateGreenDuration(highTraffic, inter);
        assertTrue("Adaptive strategy scales green duration", calcGreen > 20);

        // Generic filtering with Lambda Predicate
        List<Vehicle> fleet = List.of(v1, v2);
        List<Vehicle> highPri = AnalyticsUtils.filter(fleet, v -> v.calculatePriority() > 5);
        assertTrue("Lambda filter Predicate", highPri.size() == 1 && highPri.get(0).getId().equals("V-2"));
    }

    // ==========================================
    // UNIT 3: Packages, Exceptions & Strings
    // ==========================================
    private static void testUnit3_Packages_Exceptions_Strings() {
        System.out.println("\n--- [UNIT 3] Packages, Exception Handling & Strings ---");

        // Custom checked exception with cause chaining
        boolean caughtChained = false;
        try {
            throw new SensorReadException("Telemetry probe broken", "SNS-ERR", new IOException("Hardware timeout"));
        } catch (SensorReadException e) {
            caughtChained = (e.getCause() instanceof IOException) && e.getSensorId().equals("SNS-ERR");
        }
        assertTrue("Custom checked exception with cause chaining", caughtChained);

        // Custom unchecked exception
        boolean caughtUnchecked = false;
        try {
            TrafficSignal sig = new TrafficSignal("SIG-FAULT", "INT-TEST", "NORTH");
            sig.setState(null, 10); // should throw SignalFailureException
        } catch (SignalFailureException e) {
            caughtUnchecked = e.getSignalId().equals("SIG-FAULT");
        }
        assertTrue("Custom unchecked exception SignalFailureException", caughtUnchecked);

        // String processing & Regex plate validation
        ReportGenerator rg = new ReportGenerator();
        assertTrue("Regex license plate valid UP16AB1234", rg.validatePlateNumber("UP16AB1234"));
        assertTrue("Regex license plate valid DL01C4567", rg.validatePlateNumber("DL01C4567"));
        assertTrue("Regex license plate invalid", !rg.validatePlateNumber("INVALID-PLATE-999"));

        // StringBuilder Report synthesis
        String report = rg.generateFullReport(List.of(new Intersection("INT-R", "Report Cross", "Sec 5")), List.of());
        assertTrue("StringBuilder formatted report generated", report.contains("SMART TRAFFIC MONITORING") && report.contains("Report Cross"));
    }

    // ==========================================
    // UNIT 4: Multithreading, I/O Streams, Sockets
    // ==========================================
    private static void testUnit4_Multithreading_IO_Sockets() {
        System.out.println("\n--- [UNIT 4] Multithreading, I/O Streams & Sockets ---");

        // 1. Byte Streams (DataOutputStream / DataInputStream)
        String datPath = "data/logs/test_sensor.dat";
        SensorData writeData = new SensorData("SNS-BIN1", "INT-001", 17, 38.5, 123456789L);
        try {
            FileIOHandler.appendSensorDataBinary(writeData, datPath);
            List<SensorData> readList = FileIOHandler.readAllSensorDataBinary(datPath);
            assertTrue("Byte streams binary read/write matches",
                    !readList.isEmpty() && readList.get(0).getSensorId().equals("SNS-BIN1") && readList.get(0).getVehicleCount() == 17);
        } catch (SensorReadException e) {
            assertTrue("Byte streams failed: " + e.getMessage(), false);
        }

        // 2. Character Streams (PrintWriter / BufferedReader)
        String csvPath = "data/exports/test_export.csv";
        try {
            FileIOHandler.writeCsvReport(List.of(new String[]{"ID", "Speed"}, new String[]{"V1", "55.5"}), csvPath);
            List<String> lines = FileIOHandler.readTextLines(csvPath);
            assertTrue("Character streams CSV read/write matches",
                    lines.size() >= 2 && lines.get(0).equals("ID,Speed"));
        } catch (TrafficSystemException e) {
            assertTrue("Character streams failed: " + e.getMessage(), false);
        }

        // 3. Object Serialization
        String serPath = "data/logs/test_backup.ser";
        try {
            FileIOHandler.serializeObject(writeData, serPath);
            SensorData restored = FileIOHandler.deserializeObject(serPath, SensorData.class);
            assertTrue("Object Serialization / Deserialization matches",
                    restored != null && restored.getSensorId().equals("SNS-BIN1"));
        } catch (TrafficSystemException e) {
            assertTrue("Serialization failed: " + e.getMessage(), false);
        }

        // 4. Sockets & Multithreading (TCP & UDP)
        TrafficControllerService service = new TrafficControllerService();
        int testTcpPort = 9190;
        int testUdpPort = 9191;

        TrafficServer tcpServer = new TrafficServer(testTcpPort, service);
        Thread tcpThread = new Thread(tcpServer);
        tcpThread.setDaemon(true);
        tcpThread.start();

        EmergencySignalUDP udpListener = new EmergencySignalUDP(testUdpPort, service);
        Thread udpThread = new Thread(udpListener);
        udpThread.setDaemon(true);
        udpThread.start();

        try {
            Thread.sleep(300); // Allow sockets to bind

            // Test TCP Client
            MonitoringClient client = new MonitoringClient("localhost", testTcpPort);
            client.connect();
            String response = client.sendCommand("GET_STATUS INT-001");
            assertTrue("TCP Client-Server communication successful", response != null && response.startsWith("OK [INT-001"));
            client.disconnect();

            // Test UDP Broadcast
            udpListener.broadcastEmergency("INT-001", "TEST-AMB-99", 10);
            Thread.sleep(300);
            TrafficSignal northSig = service.getIntersectionRepo().findById("INT-001").getSignals().get(0);
            assertTrue("UDP Emergency broadcast handled", northSig.getCurrentState() == SignalState.GREEN);

        } catch (Exception e) {
            assertTrue("Networking test failed: " + e.getMessage(), false);
        } finally {
            tcpServer.stop();
            udpListener.stop();
        }
    }

    // ==========================================
    // UNIT 5: Generics & Collections
    // ==========================================
    private static void testUnit5_Generics_Collections() {
        System.out.println("\n--- [UNIT 5] Generics & Collections Framework ---");

        // GenericRepository with bounded type <T extends Comparable<T>>
        GenericRepository<StandardVehicle> repo = new GenericRepository<>();
        StandardVehicle sv1 = new StandardVehicle("SV-1", "UP16A1111", "Car", "PETROL");
        StandardVehicle sv2 = new StandardVehicle("SV-2", "UP16B2222", "Bus", "DIESEL");

        repo.save("SV-1", sv1);
        repo.save("SV-2", sv2);

        assertTrue("Generic repository storage & retrieval", repo.count() == 2 && repo.findById("SV-1") != null);

        // TreeSet sorted retrieval (using Comparable)
        TreeSet<StandardVehicle> sorted = repo.findSorted();
        assertTrue("TreeSet sorted retrieval with bounded Comparable", !sorted.isEmpty() && sorted.first().calculatePriority() == 3);

        // Explicit Iterator usage in deleteById
        boolean deleted = repo.deleteById("SV-1");
        assertTrue("Iterator-backed deletion", deleted && repo.count() == 1 && !repo.exists("SV-1"));

        // TreeMap ranking in AnalyticsUtils
        Intersection i1 = new Intersection("I-1", "Low Cross", "L1");
        Intersection i2 = new Intersection("I-2", "High Cross", "L2");
        i1.setLaneDensity(0, 0, 2);
        i2.setLaneDensity(0, 0, 15);

        Map<Double, Intersection> ranked = AnalyticsUtils.rankByCongestion(List.of(i1, i2));
        assertTrue("TreeMap congestion ranking", !ranked.isEmpty());

        // VehicleRepository specialized methods
        VehicleRepository vRepo = new VehicleRepository();
        EmergencyVehicle ev = new EmergencyVehicle("EV-1", "DL01E1234", "POLICE", 8);
        vRepo.save(ev.getId(), ev);
        vRepo.save(sv2.getId(), sv2);

        List<EmergencyVehicle> emergencies = vRepo.findEmergencyVehicles();
        assertTrue("Specialized VehicleRepository findEmergencyVehicles", emergencies.size() == 1);
        assertTrue("VehicleRepository findByPlate", vRepo.findByPlate("DL01E1234") != null);
    }
}
