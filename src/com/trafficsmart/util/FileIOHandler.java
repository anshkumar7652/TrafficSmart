package com.trafficsmart.util;

import com.trafficsmart.exception.SensorReadException;
import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.SensorData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing robust File I/O operations for the traffic management platform.
 * Fully demonstrates Unit 4 syllabus requirements:
 * 1. Byte Streams (DataOutputStream, DataInputStream, FileOutputStream, FileInputStream)
 * 2. Character Streams (BufferedReader, PrintWriter, FileReader, FileWriter)
 * 3. Object Serialization (ObjectOutputStream, ObjectInputStream)
 * 4. Exception Handling with try-with-resources, cause chaining, and multi-catch.
 *
 * @author Ansh
 * @version 1.0
 */
public final class FileIOHandler {

    private FileIOHandler() {
        // Prevent instantiation of utility class
    }

    // ==========================================
    // BYTE STREAMS: Binary Data Log Persistence
    // ==========================================

    /**
     * Appends a {@link SensorData} record to a binary file using Byte Streams ({@link DataOutputStream}).
     *
     * @param data sensor data entity
     * @param filePath path to binary log file
     * @throws SensorReadException if an I/O write error occurs
     */
    public static void appendSensorDataBinary(SensorData data, String filePath) throws SensorReadException {
        if (data == null || filePath == null) return;
        ensureParentDirectory(filePath);

        try (FileOutputStream fos = new FileOutputStream(filePath, true);
             DataOutputStream dos = new DataOutputStream(fos)) {

            dos.writeUTF(data.getSensorId());
            dos.writeUTF(data.getIntersectionId());
            dos.writeInt(data.getVehicleCount());
            dos.writeDouble(data.getAvgSpeed());
            dos.writeLong(data.getTimestamp());
            dos.flush();

        } catch (IOException e) {
            throw new SensorReadException("Failed to write binary sensor telemetry", data.getSensorId(), e);
        }
    }

    /**
     * Reads all {@link SensorData} records from a binary file using Byte Streams ({@link DataInputStream}).
     *
     * @param filePath path to binary log file
     * @return list of parsed SensorData instances
     * @throws SensorReadException if an I/O read error occurs
     */
    public static List<SensorData> readAllSensorDataBinary(String filePath) throws SensorReadException {
        List<SensorData> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return records;
        }

        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream dis = new DataInputStream(fis)) {

            while (dis.available() > 0) {
                try {
                    String sensorId = dis.readUTF();
                    String intersectionId = dis.readUTF();
                    int count = dis.readInt();
                    double speed = dis.readDouble();
                    long timestamp = dis.readLong();

                    records.add(new SensorData(sensorId, intersectionId, count, speed, timestamp));
                } catch (EOFException eof) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new SensorReadException("Error parsing binary sensor log", filePath, e);
        }

        return records;
    }

    // ==========================================
    // OBJECT SERIALIZATION: System State Backup
    // ==========================================

    /**
     * Serializes an object to disk using {@link ObjectOutputStream}.
     *
     * @param object serializable state object
     * @param filePath output destination
     * @throws TrafficSystemException on serialization failure
     */
    public static void serializeObject(Serializable object, String filePath) throws TrafficSystemException {
        ensureParentDirectory(filePath);
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(object);
            oos.flush();

        } catch (IOException e) {
            throw new TrafficSystemException("ERR_SERIALIZE", "Failed to backup object state to " + filePath, e);
        }
    }

    /**
     * Deserializes an object from disk using {@link ObjectInputStream}.
     *
     * @param filePath input source
     * @param clazz expected class type
     * @param <T> target generic type
     * @return deserialized instance
     * @throws TrafficSystemException on read or class not found error
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(String filePath, Class<T> clazz) throws TrafficSystemException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new TrafficSystemException("ERR_BACKUP_NOT_FOUND", "Backup file does not exist: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Object obj = ois.readObject();
            if (clazz.isInstance(obj)) {
                return (T) obj;
            } else {
                throw new TrafficSystemException("ERR_TYPE_MISMATCH", "Deserialized object is not of type: " + clazz.getName());
            }

        } catch (IOException | ClassNotFoundException e) { // Multi-catch demonstration
            throw new TrafficSystemException("ERR_DESERIALIZE", "Failed to restore backup from " + filePath, e);
        }
    }

    // ==========================================
    // CHARACTER STREAMS: CSV & Reports
    // ==========================================

    /**
     * Writes tabular data into a CSV file using Character Streams ({@link PrintWriter}/{@link BufferedWriter}).
     *
     * @param rows list of column string arrays
     * @param filePath destination path
     * @throws TrafficSystemException on file write errors
     */
    public static void writeCsvReport(List<String[]> rows, String filePath) throws TrafficSystemException {
        ensureParentDirectory(filePath);
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            for (String[] row : rows) {
                pw.println(String.join(",", escapeCsvRow(row)));
            }
            pw.flush();

        } catch (IOException e) {
            throw new TrafficSystemException("ERR_CSV_WRITE", "Unable to export CSV report to " + filePath, e);
        }
    }

    /**
     * Reads lines from a text/CSV file using Character Streams ({@link BufferedReader}/{@link FileReader}).
     *
     * @param filePath source path
     * @return list of text lines
     * @throws TrafficSystemException on read error
     */
    public static List<String> readTextLines(String filePath) throws TrafficSystemException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return lines;
        }

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            throw new TrafficSystemException("ERR_FILE_READ", "Unable to read lines from " + filePath, e);
        }
        return lines;
    }

    private static String[] escapeCsvRow(String[] row) {
        String[] escaped = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            String val = row[i] == null ? "" : row[i];
            if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
                val = "\"" + val.replace("\"", "\"\"") + "\"";
            }
            escaped[i] = val;
        }
        return escaped;
    }

    private static void ensureParentDirectory(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
