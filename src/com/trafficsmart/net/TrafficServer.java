package com.trafficsmart.net;

import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SignalState;
import com.trafficsmart.service.TrafficControllerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Multi-threaded TCP Server providing remote administrative control and monitoring of traffic signals.
 * Demonstrates Unit 4 socket programming: {@link ServerSocket}, client handling threads,
 * and text-based network protocol parsing.
 *
 * @author Ansh
 * @version 1.0
 */
public class TrafficServer implements Runnable {

    private final int port;
    private final TrafficControllerService controller;
    private volatile boolean running;
    private ServerSocket serverSocket;

    /**
     * Constructs a TrafficServer on the specified TCP port.
     *
     * @param port TCP listening port
     * @param controller system service reference
     */
    public TrafficServer(int port, TrafficControllerService controller) {
        this.port = port;
        this.controller = controller;
        this.running = false;
    }

    @Override
    public void run() {
        this.running = true;
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("[TCP SERVER] Traffic management server listening on port " + port);

            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Spawn client handler thread for each incoming TCP client
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, controller), "TCPClient-" + clientSocket.getRemoteSocketAddress());
                    clientThread.setDaemon(true);
                    clientThread.start();
                } catch (IOException e) {
                    if (!running) break;
                    System.err.println("[TCP SERVER] Accept failed: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[TCP SERVER] Server socket initialization error on port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Stops the TCP server and releases socket resources.
     */
    public synchronized void stop() {
        this.running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
        System.out.println("[TCP SERVER] Server halted.");
    }

    /**
     * Inner Runnable managing bidirectional TCP communication with an individual client.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final TrafficControllerService controller;

        public ClientHandler(Socket socket, TrafficControllerService controller) {
            this.socket = socket;
            this.controller = controller;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                writer.println("OK 200 Welcome to Smart Traffic Server. Ready for commands.");
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("QUIT")) {
                        writer.println("OK Goodbye.");
                        break;
                    }

                    String response = processCommand(line);
                    writer.println(response);
                }

            } catch (IOException e) {
                System.out.println("[TCP SERVER] Client disconnected: " + socket.getRemoteSocketAddress());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private String processCommand(String cmd) {
            String[] tokens = cmd.split("\\s+");
            if (tokens.length == 0 || tokens[0].isEmpty()) {
                return "ERROR Empty command received";
            }

            String action = tokens[0].toUpperCase();
            try {
                switch (action) {
                    case "GET_STATUS" -> {
                        if (tokens.length < 2) return "ERROR Usage: GET_STATUS <intersection_id>";
                        String id = tokens[1];
                        Intersection inter = controller.getIntersectionRepo().findById(id);
                        if (inter == null) return "ERROR Intersection not found: " + id;
                        return "OK " + inter.getStatus();
                    }

                    case "SET_SIGNAL" -> {
                        // SET_SIGNAL <intersection_id> <signal_id> <RED|YELLOW|GREEN>
                        if (tokens.length < 4) return "ERROR Usage: SET_SIGNAL <intersection_id> <signal_id> <STATE>";
                        String intId = tokens[1];
                        String sigId = tokens[2];
                        SignalState state = SignalState.valueOf(tokens[3].toUpperCase());
                        controller.setManualSignalState(intId, sigId, state);
                        return "OK Signal " + sigId + " updated to " + state;
                    }

                    case "EMERGENCY" -> {
                        // EMERGENCY <intersection_id> <vehicle_id>
                        if (tokens.length < 3) return "ERROR Usage: EMERGENCY <intersection_id> <vehicle_id>";
                        controller.handleEmergencyOverride(tokens[1], tokens[2]);
                        return "OK Emergency override activated for " + tokens[2] + " at " + tokens[1];
                    }

                    case "GET_REPORT" -> {
                        return "OK\n" + controller.generateSystemReport();
                    }

                    default -> {
                        return "ERROR Unknown command: " + action + ". Supported: GET_STATUS, SET_SIGNAL, EMERGENCY, GET_REPORT, QUIT";
                    }
                }
            } catch (IllegalArgumentException e) {
                return "ERROR Invalid parameter value: " + e.getMessage();
            } catch (TrafficSystemException e) {
                return "ERROR " + e.getMessage();
            }
        }
    }
}
