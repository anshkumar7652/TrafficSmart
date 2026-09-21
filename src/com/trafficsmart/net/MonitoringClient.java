package com.trafficsmart.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Remote TCP Monitoring Client for connecting to the TrafficServer.
 * Demonstrates Unit 4 socket client programming: {@link Socket}, {@link BufferedReader}, and {@link PrintWriter}.
 *
 * @author Ansh
 * @version 1.0
 */
public class MonitoringClient {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a client targeting the given host and port.
     *
     * @param host server address
     * @param port server TCP port
     */
    public MonitoringClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Connects to the remote server and performs initial handshake.
     *
     * @throws IOException on connection failure
     */
    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);

        // Read initial welcome greeting
        String greeting = in.readLine();
        System.out.println("[CLIENT] Server response: " + greeting);
    }

    /**
     * Sends a command line to the server and returns the line response.
     *
     * @param command text command (e.g. "GET_STATUS INT-001")
     * @return response string
     * @throws IOException if transmission fails
     */
    public String sendCommand(String command) throws IOException {
        if (out == null || in == null) {
            throw new IOException("Client is not connected.");
        }
        out.println(command);
        return in.readLine();
    }

    /**
     * Closes the socket connection gracefully.
     */
    public void disconnect() {
        try {
            if (out != null) out.println("QUIT");
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
        System.out.println("[CLIENT] Disconnected.");
    }

    /**
     * Standalone CLI runner for the remote monitoring client.
     *
     * @param args host [arg 0] and port [arg 1]
     */
    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : 9090;

        System.out.println("[CLIENT] Connecting to Traffic Server at " + host + ":" + port + "...");
        MonitoringClient client = new MonitoringClient(host, port);

        try {
            client.connect();
            System.out.println("[CLIENT] Connected! Enter commands (GET_STATUS INT-001, SET_SIGNAL INT-001 SIG-101 GREEN, EMERGENCY INT-001 VH-AMB1, QUIT):");

            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("client> ");
                    if (!scanner.hasNextLine()) break;
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    if (line.equalsIgnoreCase("QUIT") || line.equalsIgnoreCase("EXIT")) {
                        client.disconnect();
                        break;
                    }

                    String response = client.sendCommand(line);
                    System.out.println("< " + response);
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT ERROR] Connection failed: " + e.getMessage());
        }
    }
}
