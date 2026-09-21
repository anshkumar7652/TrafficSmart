package com.trafficsmart.gui;

import com.trafficsmart.model.EmergencyVehicle;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.model.SignalState;
import com.trafficsmart.model.TrafficSignal;
import com.trafficsmart.service.TrafficControllerService;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Custom graphical canvas rendering real-time animated 2D intersection dynamics.
 * Demonstrates Unit 5 GUI requirements:
 * 1. Extending {@link JPanel}
 * 2. Overriding {@code paintComponent(Graphics g)} with {@link Graphics2D}
 * 3. Smooth animation using {@link javax.swing.Timer}
 * 4. Rich aesthetic rendering with anti-aliasing and color blending.
 *
 * @author Ansh
 * @version 1.0
 */
public class IntersectionCanvas extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final Timer animationTimer;
    private String selectedIntersectionId;
    private boolean strobeState;

    /**
     * Constructs the canvas and initializes animation repainting.
     *
     * @param controller system service reference
     */
    @SuppressWarnings("this-escape")
    public IntersectionCanvas(TrafficControllerService controller) {
        this.controller = controller;
        this.selectedIntersectionId = "INT-001";
        this.strobeState = false;

        setPreferredSize(new Dimension(750, 550));
        setBackground(new Color(26, 32, 44)); // Modern dark charcoal background

        // Animation loop running every 500ms
        this.animationTimer = new Timer(500, e -> {
            this.strobeState = !this.strobeState;
            repaint();
        });
        this.animationTimer.start();
    }

    public void setSelectedIntersectionId(String intersectionId) {
        this.selectedIntersectionId = intersectionId;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable high-quality anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int roadWidth = 160;

        Intersection currentIntersection = controller.getIntersectionRepo().findById(selectedIntersectionId);
        if (currentIntersection == null) {
            List<Intersection> list = controller.getIntersectionRepo().findAll();
            if (!list.isEmpty()) {
                currentIntersection = list.get(0);
                selectedIntersectionId = currentIntersection.getIntersectionId();
            }
        }

        // 1. Draw Asphalt Road Cross
        g2d.setColor(new Color(45, 55, 72));
        // Vertical roadway
        g2d.fillRect(centerX - roadWidth / 2, 40, roadWidth, height - 80);
        // Horizontal roadway
        g2d.fillRect(30, centerY - roadWidth / 2, width - 60, roadWidth);

        // 2. Draw Yellow Road Dividing Markings
        g2d.setColor(new Color(236, 201, 75));
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{12.0f, 10.0f}, 0.0f));
        // Vertical dividing line
        g2d.drawLine(centerX, 40, centerX, centerY - roadWidth / 2);
        g2d.drawLine(centerX, centerY + roadWidth / 2, centerX, height - 40);
        // Horizontal dividing line
        g2d.drawLine(30, centerY, centerX - roadWidth / 2, centerY);
        g2d.drawLine(centerX + roadWidth / 2, centerY, width - 30, centerY);

        // 3. Draw Zebra Crosswalks
        drawCrosswalk(g2d, centerX - roadWidth / 2, centerY - roadWidth / 2 - 18, roadWidth, 14, true);
        drawCrosswalk(g2d, centerX - roadWidth / 2, centerY + roadWidth / 2 + 4, roadWidth, 14, true);
        drawCrosswalk(g2d, centerX - roadWidth / 2 - 18, centerY - roadWidth / 2, 14, roadWidth, false);
        drawCrosswalk(g2d, centerX + roadWidth / 2 + 4, centerY - roadWidth / 2, 14, roadWidth, false);

        // 4. Render Traffic Lights at Cardinal Approaches
        if (currentIntersection != null) {
            for (TrafficSignal signal : currentIntersection.getSignals()) {
                switch (signal.getApproachDirection().toUpperCase()) {
                    case "NORTH" -> drawTrafficLight(g2d, centerX + roadWidth / 2 + 10, centerY - roadWidth / 2 - 80, signal);
                    case "SOUTH" -> drawTrafficLight(g2d, centerX - roadWidth / 2 - 50, centerY + roadWidth / 2 + 10, signal);
                    case "EAST"  -> drawTrafficLight(g2d, centerX + roadWidth / 2 + 10, centerY + roadWidth / 2 + 10, signal);
                    case "WEST"  -> drawTrafficLight(g2d, centerX - roadWidth / 2 - 50, centerY - roadWidth / 2 - 80, signal);
                }
            }

            // 5. Draw Queued Vehicle Representations
            drawVehicles(g2d, centerX, centerY, roadWidth, currentIntersection);

            // 6. Draw HUD Header and Congestion Indicator
            drawHUD(g2d, width, height, currentIntersection);
        }

        g2d.dispose();
    }

    private void drawCrosswalk(Graphics2D g2d, int x, int y, int w, int h, boolean horizontal) {
        g2d.setColor(new Color(226, 232, 240, 180));
        g2d.setStroke(new BasicStroke(1.0f));
        if (horizontal) {
            for (int i = 0; i < w; i += 16) {
                g2d.fillRect(x + i, y, 8, h);
            }
        } else {
            for (int i = 0; i < h; i += 16) {
                g2d.fillRect(x, y + i, w, 8);
            }
        }
    }

    private void drawTrafficLight(Graphics2D g2d, int x, int y, TrafficSignal signal) {
        // Enclosure box
        g2d.setColor(new Color(15, 23, 42));
        g2d.fill(new RoundRectangle2D.Double(x, y, 36, 75, 10, 10));
        g2d.setColor(new Color(100, 116, 139));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new RoundRectangle2D.Double(x, y, 36, 75, 10, 10));

        SignalState state = signal.getCurrentState();

        // Red Lamp
        Color redColor = (state == SignalState.RED) ? new Color(239, 68, 68) : new Color(60, 20, 20);
        g2d.setColor(redColor);
        g2d.fillOval(x + 9, y + 6, 18, 18);

        // Yellow Lamp
        Color yellowColor = (state == SignalState.YELLOW) ? new Color(234, 179, 8) : new Color(60, 50, 15);
        g2d.setColor(yellowColor);
        g2d.fillOval(x + 9, y + 28, 18, 18);

        // Green Lamp
        Color greenColor = (state == SignalState.GREEN) ? new Color(34, 197, 94) : new Color(15, 55, 25);
        g2d.setColor(greenColor);
        g2d.fillOval(x + 9, y + 50, 18, 18);

        // Label
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.setColor(Color.WHITE);
        g2d.drawString(signal.getApproachDirection().substring(0, 1) + " [" + signal.getCurrentDurationSeconds() + "s]", x - 2, y + 90);
    }

    private void drawVehicles(Graphics2D g2d, int cx, int cy, int roadWidth, Intersection inter) {
        int[][] matrix = inter.getLaneDensityMatrix();

        // Render queue representations on approaches
        // North approach (flowing South)
        int northCount = matrix.length > 0 ? (matrix[0][0] + matrix[0][1] + (matrix[0].length > 2 ? matrix[0][2] : 0)) : 4;
        for (int i = 0; i < Math.min(6, northCount); i++) {
            drawCar(g2d, cx - 35, cy - roadWidth / 2 - 35 - (i * 24), 20, 16, new Color(59, 130, 246), false);
        }

        // South approach (flowing North)
        int southCount = matrix.length > 1 ? (matrix[1][0] + matrix[1][1] + (matrix[1].length > 2 ? matrix[1][2] : 0)) : 3;
        for (int i = 0; i < Math.min(6, southCount); i++) {
            drawCar(g2d, cx + 15, cy + roadWidth / 2 + 20 + (i * 24), 20, 16, new Color(168, 85, 247), false);
        }

        // Check if any emergency vehicle exists in the area
        List<EmergencyVehicle> emergencies = controller.getVehicleRepo().findEmergencyVehicles();
        if (!emergencies.isEmpty() && strobeState) {
            // Draw emergency vehicle with glowing strobe near center
            drawCar(g2d, cx - 35, cy - 25, 24, 18, Color.RED, true);
        }
    }

    private void drawCar(Graphics2D g2d, int x, int y, int w, int h, Color color, boolean isEmergency) {
        g2d.setColor(color);
        g2d.fill(new RoundRectangle2D.Double(x, y, w, h, 6, 6));
        g2d.setColor(Color.BLACK);
        g2d.draw(new RoundRectangle2D.Double(x, y, w, h, 6, 6));

        if (isEmergency) {
            g2d.setColor(Color.CYAN);
            g2d.fillOval(x + w / 2 - 3, y + h / 2 - 3, 6, 6);
        }
    }

    private void drawHUD(Graphics2D g2d, int width, int height, Intersection inter) {
        // Top HUD banner
        g2d.setColor(new Color(15, 23, 42, 220));
        g2d.fillRect(0, 0, width, 40);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("INTERSECTION: %s (%s)", inter.getName(), inter.getIntersectionId()), 20, 25);

        // Congestion Gauge
        double congestion = inter.getCongestionLevel();
        Color gaugeColor = (congestion > 70) ? new Color(239, 68, 68) :
                           (congestion > 40) ? new Color(234, 179, 8) : new Color(34, 197, 94);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("Congestion:", width - 260, 25);

        g2d.setColor(new Color(51, 65, 85));
        g2d.fill(new RoundRectangle2D.Double(width - 170, 12, 100, 16, 8, 8));
        g2d.setColor(gaugeColor);
        g2d.fill(new RoundRectangle2D.Double(width - 170, 12, (int)(congestion), 16, 8, 8));

        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("%.1f%%", congestion), width - 60, 25);
    }
}
