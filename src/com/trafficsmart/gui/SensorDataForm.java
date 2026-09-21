package com.trafficsmart.gui;

import com.trafficsmart.exception.SensorReadException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SensorData;
import com.trafficsmart.service.TrafficControllerService;
import com.trafficsmart.util.FileIOHandler;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Data entry form providing manual sensor calibration and telemetry ingestion.
 * Demonstrates Unit 5 GUI requirements:
 * 1. Data entry forms with input fields ({@link JTextField})
 * 2. Form field validation and error alerting
 * 3. Direct integration with binary file logging via {@link FileIOHandler}.
 *
 * @author Ansh
 * @version 1.0
 */
public class SensorDataForm extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final JTextField sensorIdField;
    private final JComboBox<String> intersectionBox;
    private final JTextField vehicleCountField;
    private final JTextField avgSpeedField;

    /**
     * Constructs the sensor telemetry submission form.
     *
     * @param controller system service orchestrator
     */
    @SuppressWarnings("this-escape")
    public SensorDataForm(TrafficControllerService controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        setBackground(new Color(248, 250, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header Title
        JLabel title = new JLabel("Roadside Sensor Telemetry Ingestion");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        // 1. Sensor ID
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Sensor ID (e.g. SNS-101):"), gbc);
        sensorIdField = new JTextField("SNS-USR-" + System.currentTimeMillis() % 10000, 15);
        gbc.gridx = 1; gbc.gridy = 1;
        add(sensorIdField, gbc);

        // 2. Intersection
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Target Intersection:"), gbc);
        intersectionBox = new JComboBox<>();
        for (Intersection inter : controller.getIntersectionRepo().findAll()) {
            intersectionBox.addItem(inter.getIntersectionId() + " - " + inter.getName());
        }
        gbc.gridx = 1; gbc.gridy = 2;
        add(intersectionBox, gbc);

        // 3. Vehicle Count
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Observed Vehicle Count:"), gbc);
        vehicleCountField = new JTextField("18", 15);
        gbc.gridx = 1; gbc.gridy = 3;
        add(vehicleCountField, gbc);

        // 4. Avg Speed
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Observed Avg Speed (km/h):"), gbc);
        avgSpeedField = new JTextField("32.5", 15);
        gbc.gridx = 1; gbc.gridy = 4;
        add(avgSpeedField, gbc);

        // Submit Button
        JButton submitButton = new JButton("Ingest Telemetry Record");
        submitButton.setBackground(new Color(16, 185, 129));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(submitButton, gbc);

        submitButton.addActionListener(e -> onSubmit());
    }

    private void onSubmit() {
        String sensorId = sensorIdField.getText().trim();
        String selInt = (String) intersectionBox.getSelectedItem();
        String countStr = vehicleCountField.getText().trim();
        String speedStr = avgSpeedField.getText().trim();

        if (sensorId.isEmpty() || selInt == null || countStr.isEmpty() || speedStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All form fields are mandatory.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int count = Integer.parseInt(countStr);
            double speed = Double.parseDouble(speedStr);

            if (count < 0 || speed < 0) {
                JOptionPane.showMessageDialog(this, "Counts and speeds must be non-negative values.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String intId = selInt.split(" - ")[0];
            SensorData data = new SensorData(sensorId, intId, count, speed);

            // Write to binary stream log
            FileIOHandler.appendSensorDataBinary(data, "data/logs/sensor_manual_ingest.dat");

            // Update intersection lane density
            Intersection inter = controller.getIntersectionRepo().findById(intId);
            if (inter != null) {
                inter.setLaneDensity(0, 0, count / 2);
                inter.setLaneDensity(1, 0, count / 2);
            }

            controller.addEvent("Manual telemetry ingested from " + sensorId + " for " + intId + " (" + count + " veh, " + speed + " km/h)");

            JOptionPane.showMessageDialog(this,
                    "Telemetry recorded successfully!\nBinary record appended to data/logs/sensor_manual_ingest.dat",
                    "Ingestion Complete", JOptionPane.INFORMATION_MESSAGE);

            // Refresh default sensor ID for next entry
            sensorIdField.setText("SNS-USR-" + System.currentTimeMillis() % 10000);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vehicle count and speed must be valid numerical values.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (SensorReadException ex) {
            JOptionPane.showMessageDialog(this, "Binary file logging failed: " + ex.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
