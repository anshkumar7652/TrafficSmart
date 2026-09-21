package com.trafficsmart.gui;

import com.trafficsmart.service.TrafficControllerService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

/**
 * Main administrative desktop dashboard for the Smart Traffic Monitoring And Management System.
 * Demonstrates Unit 5 GUI requirements:
 * 1. {@link JFrame} main window
 * 2. {@link JTabbedPane} with multiple functional panels
 * 3. {@link JMenuBar}, {@link JMenu}, and {@link JMenuItem}
 * 4. {@link BorderLayout} and layout management
 * 5. Event dispatching and window lifecycle management.
 *
 * @author Ansh
 * @version 1.0
 */
public class MainDashboard extends JFrame {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final JTabbedPane tabbedPane;
    private final IntersectionCanvas canvas;
    private final SignalControlPanel controlPanel;
    private final SensorDataForm sensorForm;
    private final ReportViewPanel reportPanel;
    private final DefaultListModel<String> eventListModel;

    /**
     * Constructs and displays the main dashboard.
     *
     * @param controller system service orchestrator
     */
    @SuppressWarnings("this-escape")
    public MainDashboard(TrafficControllerService controller) {
        super("Smart Traffic Monitoring and Management System (NIET Capstone Project)");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null); // Center on screen

        // Initialize Menu Bar
        setJMenuBar(createMenuBar());

        // Initialize Tabbed Components
        this.canvas = new IntersectionCanvas(controller);
        this.controlPanel = new SignalControlPanel(controller, canvas);
        this.sensorForm = new SensorDataForm(controller);
        this.reportPanel = new ReportViewPanel(controller);
        this.eventListModel = new DefaultListModel<>();

        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Add Tabs
        tabbedPane.addTab("🚦 Live Intersection Monitor", canvas);
        tabbedPane.addTab("🕹️ Signal Control Console", controlPanel);
        tabbedPane.addTab("📡 Sensor Data Ingestion", sensorForm);
        tabbedPane.addTab("📊 Reports & Analytics", reportPanel);
        tabbedPane.addTab("📜 Live Event Log", createEventLogPanel());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Status bar at bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBar.setBackground(new Color(241, 245, 249));

        JLabel statusText = new JLabel("System Status: Operational | Server listening on TCP 9090 & UDP 9091");
        statusText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusText.setForeground(new Color(71, 85, 105));
        statusBar.add(statusText, BorderLayout.WEST);

        getContentPane().add(statusBar, BorderLayout.SOUTH);

        // Periodic timer to refresh event log tab
        Timer eventRefreshTimer = new Timer(1500, e -> updateEventLog());
        eventRefreshTimer.start();

        // Register window closing hook for graceful thread shutdown
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controller.shutdownSystem();
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export CSV Report");
        exportItem.addActionListener(e -> {
            tabbedPane.setSelectedComponent(reportPanel);
            reportPanel.refreshData();
        });

        JMenuItem exitItem = new JMenuItem("Exit Application");
        exitItem.addActionListener(e -> {
            controller.shutdownSystem();
            System.exit(0);
        });

        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Control Menu
        JMenu controlMenu = new JMenu("Control");
        JMenuItem startSim = new JMenuItem("Start Simulation");
        startSim.addActionListener(e -> {
            controller.startSystem();
            JOptionPane.showMessageDialog(this, "Simulation & Signal timers started!", "Engine Active", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem emergencyOverrideItem = new JMenuItem("Trigger Global Emergency Transit");
        emergencyOverrideItem.addActionListener(e -> {
            controller.handleEmergencyOverride("INT-001", "PRIORITY-EV-GLOBAL");
            JOptionPane.showMessageDialog(this, "Global emergency override dispatched!", "Priority Active", JOptionPane.WARNING_MESSAGE);
        });

        controlMenu.add(startSim);
        controlMenu.add(emergencyOverrideItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About System");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Smart Traffic Monitoring and Management System\n" +
                "Capstone Project (Topic 76) - NIET Greater Noida\n" +
                "Course: Object Oriented Techniques using Java\n" +
                "Features: OOAD, Multi-threading, Socket Programming, GUI, Generics & Collections.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(controlMenu);
        bar.add(helpMenu);
        return bar;
    }

    private JPanel createEventLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 250, 252));

        JList<String> eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        eventList.setBackground(new Color(15, 23, 42));
        eventList.setForeground(new Color(148, 163, 184));

        JScrollPane scroll = new JScrollPane(eventList);
        scroll.setBorder(BorderFactory.createTitledBorder("FIFO Real-Time System Notification Stream"));
        panel.add(scroll, BorderLayout.CENTER);

        updateEventLog();
        return panel;
    }

    private void updateEventLog() {
        List<String> events = controller.getRecentEvents(40);
        eventListModel.clear();
        for (String ev : events) {
            eventListModel.addElement(ev);
        }
    }
}
