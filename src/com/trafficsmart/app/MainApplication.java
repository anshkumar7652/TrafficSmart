package com.trafficsmart.app;

import com.trafficsmart.config.SystemConfig;
import com.trafficsmart.gui.LoginForm;
import com.trafficsmart.net.EmergencySignalUDP;
import com.trafficsmart.net.TrafficServer;
import com.trafficsmart.service.TrafficControllerService;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Main application entry point for the Smart Traffic Monitoring and Management System.
 * Demonstrates Unit 1 requirements:
 * 1. Command-line argument parsing ({@code --mode=gui|server|cli}, {@code --port=XXXX})
 * 2. Console-based interactive fallback mode
 * 3. Thread bootstrap for networking and simulation engines
 * 4. Control statements and primitive operations.
 *
 * @author Ansh
 * @version 1.0
 */
public class MainApplication {

    /**
     * Application entry point.
     *
     * @param args command-line flags
     */
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("      SMART TRAFFIC MONITORING AND MANAGEMENT SYSTEM (CAPSTONE TOPIC 76)        ");
        System.out.println("      Course: Object Oriented Techniques using Java | NIET Greater Noida        ");
        System.out.println("================================================================================");

        SystemConfig config = SystemConfig.getInstance();
        String mode = "gui"; // Default mode

        // Parse command line arguments (Unit 1 syllabus requirement)
        for (String arg : args) {
            if (arg.startsWith("--mode=")) {
                mode = arg.substring("--mode=".length()).toLowerCase();
            } else if (arg.startsWith("--port=")) {
                try {
                    int customPort = Integer.parseInt(arg.substring("--port=".length()));
                    config.setServerPort(customPort);
                } catch (NumberFormatException e) {
                    System.err.println("[CLI WARN] Invalid port argument: " + arg);
                }
            } else if (arg.equals("--help") || arg.equals("-h")) {
                printUsage();
                return;
            }
        }

        // Initialize Core Service
        TrafficControllerService controller = new TrafficControllerService();

        // Start background Simulation and Signal Timer Threads
        if (config.isSimulationAutoStart()) {
            controller.startSystem();
        }

        // Start TCP Server on configured port (e.g. 9090)
        TrafficServer tcpServer = new TrafficServer(config.getServerPort(), controller);
        Thread tcpThread = new Thread(tcpServer, "TrafficServerTCP");
        tcpThread.setDaemon(true);
        tcpThread.start();

        // Start UDP Emergency Broadcast Listener (e.g. 9091)
        EmergencySignalUDP udpListener = new EmergencySignalUDP(config.getUdpPort(), controller);
        Thread udpThread = new Thread(udpListener, "EmergencySignalUDP");
        udpThread.setDaemon(true);
        udpThread.start();

        System.out.printf("[BOOT] Initialized in mode: %s | TCP Port: %d | UDP Port: %d%n",
                mode.toUpperCase(), config.getServerPort(), config.getUdpPort());

        // Launch selected operating mode
        switch (mode) {
            case "server" -> {
                System.out.println("[BOOT] Running in headless SERVER mode. Press Ctrl+C to terminate.");
                keepServerAlive();
            }
            case "cli" -> {
                System.out.println("[BOOT] Running in interactive CLI console mode.");
                runConsoleInterface(controller);
            }
            default -> {
                // GUI Mode (Swing EDT)
                System.out.println("[BOOT] Launching Java Swing Graphical User Interface...");
                SwingUtilities.invokeLater(() -> {
                    LoginForm loginForm = new LoginForm(controller);
                    loginForm.setVisible(true);
                });
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java com.trafficsmart.app.MainApplication [OPTIONS]");
        System.out.println("Options:");
        System.out.println("  --mode=gui      Launch desktop Swing GUI (default)");
        System.out.println("  --mode=server   Run headless background TCP/UDP service");
        System.out.println("  --mode=cli      Interactive terminal console interface");
        System.out.println("  --port=XXXX     Specify custom TCP port (default 9090)");
        System.out.println("  --help, -h      Display this manual");
    }

    private static void keepServerAlive() {
        try {
            while (true) {
                Thread.sleep(10000);
            }
        } catch (InterruptedException ignored) {}
    }

    /**
     * Interactive console-based interaction fallback (Unit 1 syllabus requirement).
     *
     * @param controller service reference
     */
    private static void runConsoleInterface(TrafficControllerService controller) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\nConsole commands available: report, status, emergency, exit");
            while (true) {
                System.out.print("\ntraffic-console> ");
                if (!scanner.hasNextLine()) break;
                String cmd = scanner.nextLine().trim();

                if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("quit")) {
                    controller.shutdownSystem();
                    System.out.println("[CONSOLE] System shut down. Exiting.");
                    break;
                } else if (cmd.equalsIgnoreCase("report")) {
                    System.out.println(controller.generateSystemReport());
                } else if (cmd.equalsIgnoreCase("status")) {
                    controller.getIntersectionRepo().findAll().forEach(i -> System.out.println(i.getStatus()));
                } else if (cmd.equalsIgnoreCase("emergency")) {
                    controller.handleEmergencyOverride("INT-001", "CLI-EMERGENCY-01");
                    System.out.println("[CONSOLE] Emergency override triggered at INT-001.");
                } else {
                    System.out.println("Unknown command: " + cmd + ". Type: report, status, emergency, exit");
                }
            }
        }
    }
}
