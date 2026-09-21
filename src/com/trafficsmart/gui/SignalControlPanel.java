package com.trafficsmart.gui;

import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SignalState;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.service.TrafficControllerService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Interactive control panel providing manual signal override and emergency dispatch commands.
 * Demonstrates Unit 5 GUI requirements:
 * 1. {@link GridBagLayout} complex layout arrangement
 * 2. Event listeners (ActionListeners with lambdas)
 * 3. User feedback via {@link JOptionPane} alert dialogs.
 *
 * @author Ansh
 * @version 1.0
 */
public class SignalControlPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final JComboBox<String> intersectionBox;
    private final JComboBox<String> signalBox;
    private final JComboBox<SignalState> stateBox;
    private final transient IntersectionCanvas canvasReference;

    /**
     * Constructs the SignalControlPanel with dependencies.
     *
     * @param controller system service orchestrator
     * @param canvas visualizer canvas to synchronize view selection
     */
    @SuppressWarnings("this-escape")
    public SignalControlPanel(TrafficControllerService controller, IntersectionCanvas canvas) {
        this.controller = controller;
        this.canvasReference = canvas;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(248, 250, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Header
        JLabel titleLabel = new JLabel("Manual Traffic Signal Control Console");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // 1. Intersection Selection
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Target Intersection:"), gbc);

        intersectionBox = new JComboBox<>();
        populateIntersections();
        gbc.gridx = 1; gbc.gridy = 1;
        add(intersectionBox, gbc);

        // 2. Signal Selection
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Approach Signal:"), gbc);

        signalBox = new JComboBox<>();
        updateSignalsForSelectedIntersection();
        gbc.gridx = 1; gbc.gridy = 2;
        add(signalBox, gbc);

        // 3. State Selection
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Desired State:"), gbc);

        stateBox = new JComboBox<>(SignalState.values());
        gbc.gridx = 1; gbc.gridy = 3;
        add(stateBox, gbc);

        // Action Buttons
        JButton applyButton = new JButton("Apply Signal State");
        applyButton.setBackground(new Color(59, 130, 246));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 4;
        add(applyButton, gbc);

        JButton emergencyButton = new JButton("Trigger Emergency Corridor");
        emergencyButton.setBackground(new Color(239, 68, 68));
        emergencyButton.setForeground(Color.WHITE);
        emergencyButton.setFocusPainted(false);
        gbc.gridx = 1; gbc.gridy = 4;
        add(emergencyButton, gbc);

        JButton clearButton = new JButton("Clear Manual Overrides");
        clearButton.setBackground(new Color(100, 116, 139));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(clearButton, gbc);

        // Event Handlers
        intersectionBox.addActionListener(e -> {
            updateSignalsForSelectedIntersection();
            String sel = (String) intersectionBox.getSelectedItem();
            if (sel != null && canvasReference != null) {
                canvasReference.setSelectedIntersectionId(sel.split(" - ")[0]);
            }
        });

        applyButton.addActionListener(e -> onApplyState());
        emergencyButton.addActionListener(e -> onTriggerEmergency());
        clearButton.addActionListener(e -> onClearOverrides());
    }

    private void populateIntersections() {
        intersectionBox.removeAllItems();
        for (Intersection inter : controller.getIntersectionRepo().findAll()) {
            intersectionBox.addItem(inter.getIntersectionId() + " - " + inter.getName());
        }
    }

    private void updateSignalsForSelectedIntersection() {
        signalBox.removeAllItems();
        String selected = (String) intersectionBox.getSelectedItem();
        if (selected == null) return;

        String intId = selected.split(" - ")[0];
        Intersection inter = controller.getIntersectionRepo().findById(intId);
        if (inter != null) {
            for (TrafficSignal sig : inter.getSignals()) {
                signalBox.addItem(sig.getSignalId() + " (" + sig.getApproachDirection() + ")");
            }
        }
    }

    private void onApplyState() {
        String selInt = (String) intersectionBox.getSelectedItem();
        String selSig = (String) signalBox.getSelectedItem();
        SignalState targetState = (SignalState) stateBox.getSelectedItem();

        if (selInt == null || selSig == null || targetState == null) {
            JOptionPane.showMessageDialog(this, "Please select an intersection and signal first.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String intId = selInt.split(" - ")[0];
        String sigId = selSig.split(" ")[0];

        try {
            controller.setManualSignalState(intId, sigId, targetState);
            JOptionPane.showMessageDialog(this, "Successfully transitioned signal " + sigId + " to " + targetState, "Command Success", JOptionPane.INFORMATION_MESSAGE);
            if (canvasReference != null) canvasReference.repaint();
        } catch (TrafficSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Override Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTriggerEmergency() {
        String selInt = (String) intersectionBox.getSelectedItem();
        if (selInt == null) return;
        String intId = selInt.split(" - ")[0];

        controller.handleEmergencyOverride(intId, "DISPATCH-AMB-01");
        JOptionPane.showMessageDialog(this, "Emergency clearance override activated for " + intId + "!\nAll cross-traffic transitioned to RED.",
                "Emergency Active", JOptionPane.WARNING_MESSAGE);
        if (canvasReference != null) canvasReference.repaint();
    }

    private void onClearOverrides() {
        String selInt = (String) intersectionBox.getSelectedItem();
        if (selInt == null) return;
        String intId = selInt.split(" - ")[0];

        Intersection inter = controller.getIntersectionRepo().findById(intId);
        if (inter != null) {
            for (TrafficSignal sig : inter.getSignals()) {
                sig.clearOverride();
            }
            controller.addEvent("Cleared manual overrides for intersection " + intId);
            JOptionPane.showMessageDialog(this, "Normal automatic signal cycling restored for " + intId, "Overrides Cleared", JOptionPane.INFORMATION_MESSAGE);
            if (canvasReference != null) canvasReference.repaint();
        }
    }
}
