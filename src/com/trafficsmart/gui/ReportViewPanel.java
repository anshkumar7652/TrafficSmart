package com.trafficsmart.gui;

import com.trafficsmart.exception.TrafficSystemException;
import com.trafficsmart.model.Intersection;
import com.trafficsmart.service.TrafficControllerService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.List;

/**
 * Analytical reporting dashboard panel displaying tabular data and formatted text summaries.
 * Demonstrates Unit 5 GUI requirements:
 * 1. {@link JTable} with a custom {@link DefaultTableModel}
 * 2. Scrollable {@link JTextArea}
 * 3. File export triggers leveraging Character Streams.
 *
 * @author Ansh
 * @version 1.0
 */
public class ReportViewPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final JTable summaryTable;
    private final DefaultTableModel tableModel;
    private final JTextArea reportTextArea;

    /**
     * Constructs the analytical reporting panel.
     *
     * @param controller system service orchestrator
     */
    @SuppressWarnings("this-escape")
    public ReportViewPanel(TrafficControllerService controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // Top Table: Intersection Metrics
        String[] columns = {"Node ID", "Intersection Name", "Sector Location", "Congestion %", "Vehicles", "Signals"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        summaryTable = new JTable(tableModel);
        summaryTable.setRowHeight(24);
        summaryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(summaryTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Metropolitan Intersection Density Status"));

        // Bottom Text Area: Detailed Formatted Output
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setBackground(new Color(15, 23, 42));
        reportTextArea.setForeground(new Color(226, 232, 240));
        JScrollPane textScroll = new JScrollPane(reportTextArea);
        textScroll.setBorder(BorderFactory.createTitledBorder("Synthesized System Audit Report"));

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, textScroll);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.35);
        add(splitPane, BorderLayout.CENTER);

        // Bottom Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(new Color(248, 250, 252));

        JButton refreshBtn = new JButton("Refresh Analytics");
        refreshBtn.setBackground(new Color(59, 130, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        JButton exportCsvBtn = new JButton("Export Report to CSV");
        exportCsvBtn.setBackground(new Color(16, 185, 129));
        exportCsvBtn.setForeground(Color.WHITE);
        exportCsvBtn.setFocusPainted(false);

        toolbar.add(refreshBtn);
        toolbar.add(exportCsvBtn);
        add(toolbar, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshData());
        exportCsvBtn.addActionListener(e -> onExportCsv());

        refreshData();
    }

    /**
     * Refreshes table contents and regenerates textual report.
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        List<Intersection> intersections = controller.getIntersectionRepo().findAll();

        for (Intersection inter : intersections) {
            tableModel.addRow(new Object[]{
                    inter.getIntersectionId(),
                    inter.getName(),
                    inter.getLocation(),
                    String.format("%.1f%%", inter.getCongestionLevel()),
                    inter.getTotalVehicles(),
                    inter.getSignals().size()
            });
        }

        reportTextArea.setText(controller.generateSystemReport());
    }

    private void onExportCsv() {
        String exportPath = "data/exports/traffic_summary_export.csv";
        try {
            controller.getReportGenerator().exportIntersectionsToCsv(controller.getIntersectionRepo().findAll(), exportPath);
            File f = new File(exportPath);
            JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + f.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (TrafficSystemException ex) {
            JOptionPane.showMessageDialog(this, "CSV Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
