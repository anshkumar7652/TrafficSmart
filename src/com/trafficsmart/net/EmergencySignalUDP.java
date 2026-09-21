package com.trafficsmart.net;

import com.trafficsmart.service.TrafficControllerService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * UDP Socket service handling asynchronous emergency vehicle broadcast telemetry.
 * Demonstrates Unit 4 UDP socket programming: {@link DatagramSocket}, {@link DatagramPacket},
 * and background packet listener thread.
 *
 * @author Ansh
 * @version 1.0
 */
public class EmergencySignalUDP implements Runnable {

    private final int port;
    private final TrafficControllerService controller;
    private volatile boolean running;
    private DatagramSocket socket;

    /**
     * Constructs the UDP receiver and broadcaster on a given UDP port.
     *
     * @param port UDP listening port
     * @param controller service orchestrator
     */
    public EmergencySignalUDP(int port, TrafficControllerService controller) {
        this.port = port;
        this.controller = controller;
        this.running = false;
    }

    @Override
    public void run() {
        this.running = true;
        try {
            this.socket = new DatagramSocket(port);
            System.out.println("[UDP LISTENER] Emergency broadcast listener active on UDP port " + port);

            byte[] buffer = new byte[1024];

            while (running && !socket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Blocking receive

                    String payload = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                    System.out.println("[UDP RECV] Received packet: " + payload);

                    processPacket(payload);
                } catch (IOException e) {
                    if (!running) break;
                    System.err.println("[UDP ERROR] Packet receive error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[UDP ERROR] Could not bind UDP socket to port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }

    private void processPacket(String payload) {
        // Expected payload format: "EMERGENCY:<intersection_id>:<vehicle_id>:<priority>"
        if (payload.startsWith("EMERGENCY:")) {
            String[] parts = payload.split(":");
            if (parts.length >= 3) {
                String intersectionId = parts[1];
                String vehicleId = parts[2];
                controller.handleEmergencyOverride(intersectionId, vehicleId);
            }
        }
    }

    /**
     * Broadcasts an emergency transit announcement to the local UDP port.
     *
     * @param intersectionId intersection needing clear corridor
     * @param vehicleId emergency vehicle identifier
     * @param priority vehicle priority tier
     * @throws IOException on transmission failure
     */
    public void broadcastEmergency(String intersectionId, String vehicleId, int priority) throws IOException {
        String message = String.format("EMERGENCY:%s:%s:%d", intersectionId, vehicleId, priority);
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket senderSocket = new DatagramSocket()) {
            InetAddress localHost = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(data, data.length, localHost, this.port);
            senderSocket.send(packet);
            System.out.println("[UDP BROADCAST] Sent emergency override packet for " + vehicleId + " -> " + intersectionId);
        }
    }

    /**
     * Stops the UDP listener and releases socket resources.
     */
    public synchronized void stop() {
        this.running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("[UDP LISTENER] UDP listener stopped.");
    }
}
