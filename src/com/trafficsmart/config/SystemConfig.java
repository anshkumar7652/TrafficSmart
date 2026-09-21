package com.trafficsmart.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton configuration manager for the Smart Traffic Monitoring and Management System.
 * Ensures consistent global settings across all application components.
 * Demonstrates the Singleton design pattern and configuration loading via character streams.
 *
 * @author Ansh
 * @version 1.0
 */
public class SystemConfig {

    private static volatile SystemConfig instance;
    private final Properties properties;

    // Default configuration values
    private int serverPort = 9090;
    private int udpPort = 9091;
    private int simulationTickMs = 1000;
    private boolean simulationAutoStart = true;
    private int defaultGreenDuration = 25;
    private int minGreenDuration = 10;
    private int maxGreenDuration = 60;
    private int yellowDuration = 5;
    private int emergencyOverrideDuration = 30;
    private double congestionCriticalThreshold = 75.0;
    private String dataLogDir = "data/logs";
    private String dataExportDir = "data/exports";

    /**
     * Private constructor to enforce Singleton pattern.
     * Attempts to read configuration properties from file, falling back to sensible defaults.
     */
    private SystemConfig() {
        this.properties = new Properties();
        loadConfiguration("data/config/system-config.properties");
    }

    /**
     * Retrieves the thread-safe global instance of SystemConfig using double-checked locking.
     *
     * @return the singleton SystemConfig instance
     */
    public static SystemConfig getInstance() {
        if (instance == null) {
            synchronized (SystemConfig.class) {
                if (instance == null) {
                    instance = new SystemConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Loads settings from a properties file using Character Streams (BufferedReader/FileReader).
     *
     * @param configPath relative or absolute path to the configuration file
     */
    public void loadConfiguration(String configPath) {
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            System.out.println("[CONFIG] Configuration file not found at " + configPath + ". Using defaults.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            properties.load(reader);
            this.serverPort = getIntProperty("server.port", this.serverPort);
            this.udpPort = getIntProperty("udp.port", this.udpPort);
            this.simulationTickMs = getIntProperty("simulation.tick.ms", this.simulationTickMs);
            this.simulationAutoStart = Boolean.parseBoolean(properties.getProperty("simulation.auto.start", "true"));
            this.defaultGreenDuration = getIntProperty("signal.default.green.duration", this.defaultGreenDuration);
            this.minGreenDuration = getIntProperty("signal.min.green", this.minGreenDuration);
            this.maxGreenDuration = getIntProperty("signal.max.green", this.maxGreenDuration);
            this.yellowDuration = getIntProperty("signal.yellow.duration", this.yellowDuration);
            this.emergencyOverrideDuration = getIntProperty("emergency.override.duration", this.emergencyOverrideDuration);
            this.congestionCriticalThreshold = getDoubleProperty("congestion.critical.threshold", this.congestionCriticalThreshold);
            this.dataLogDir = properties.getProperty("data.log.dir", this.dataLogDir);
            this.dataExportDir = properties.getProperty("data.export.dir", this.dataExportDir);

            System.out.println("[CONFIG] Loaded settings successfully from " + configPath);
        } catch (IOException e) {
            System.err.println("[CONFIG] Error reading configuration file: " + e.getMessage() + ". Defaults retained.");
        }
    }

    private int getIntProperty(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    private double getDoubleProperty(String key, double defaultValue) {
        String val = properties.getProperty(key);
        if (val != null) {
            try {
                return Double.parseDouble(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public int getServerPort() { return serverPort; }
    public void setServerPort(int serverPort) { this.serverPort = serverPort; }

    public int getUdpPort() { return udpPort; }
    public void setUdpPort(int udpPort) { this.udpPort = udpPort; }

    public int getSimulationTickMs() { return simulationTickMs; }
    public boolean isSimulationAutoStart() { return simulationAutoStart; }

    public int getDefaultGreenDuration() { return defaultGreenDuration; }
    public int getMinGreenDuration() { return minGreenDuration; }
    public int getMaxGreenDuration() { return maxGreenDuration; }
    public int getYellowDuration() { return yellowDuration; }

    public int getEmergencyOverrideDuration() { return emergencyOverrideDuration; }
    public double getCongestionCriticalThreshold() { return congestionCriticalThreshold; }

    public String getDataLogDir() { return dataLogDir; }
    public String getDataExportDir() { return dataExportDir; }
}
