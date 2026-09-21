package com.trafficsmart.gui;

import com.trafficsmart.service.TrafficControllerService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Authentication login interface securing the Smart Traffic Management platform.
 * Demonstrates Unit 5 GUI requirements:
 * 1. Dedicated login screen ({@link JFrame})
 * 2. Secure credential inputs ({@link JPasswordField})
 * 3. Smooth transition to {@link MainDashboard}.
 *
 * @author Ansh
 * @version 1.0
 */
public final class LoginForm extends JFrame {

    private static final long serialVersionUID = 1L;
    private final transient TrafficControllerService controller;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    /**
     * Constructs the login window.
     *
     * @param controller system service orchestrator
     */
    @SuppressWarnings("this-escape")
    public LoginForm(TrafficControllerService controller) {
        super("Traffic Control Center - Authentication");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Traffic Operations Center", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel subtitle = new JLabel("Please enter administrative credentials", JLabel.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        panel.add(subtitle, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField("admin", 15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField("admin123", 15);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(passwordField, gbc);

        // Buttons
        JButton loginBtn = new JButton("Secure Login");
        loginBtn.setBackground(new Color(59, 130, 246));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        JLabel hint = new JLabel("Default: admin / admin123", JLabel.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(new Color(148, 163, 184));
        gbc.gridy = 5;
        panel.add(hint, gbc);

        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        getContentPane().add(panel);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (("admin".equalsIgnoreCase(username) && "admin123".equals(password)) ||
            ("operator".equalsIgnoreCase(username) && "traffic2026".equals(password))) {

            controller.addEvent("User '" + username + "' authenticated successfully.");
            dispose(); // Close login window

            SwingUtilities.invokeLater(() -> {
                MainDashboard dashboard = new MainDashboard(controller);
                dashboard.setVisible(true);
            });

        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid operator credentials. Access Denied.",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
