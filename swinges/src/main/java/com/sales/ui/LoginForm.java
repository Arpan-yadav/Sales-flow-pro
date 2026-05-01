package com.sales.ui;

import com.sales.model.User;
import com.sales.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class LoginForm extends JFrame {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DashboardForm dashboardForm;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginForm(UserRepository userRepository, PasswordEncoder passwordEncoder, DashboardForm dashboardForm) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dashboardForm = dashboardForm;

        setTitle("SalesFlow Pro - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("SalesFlow Pro", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setBounds(0, 30, 400, 40);
        titleLabel.setForeground(new Color(15, 23, 42));
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 90, 300, 25);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 115, 300, 35);
        panel.add(emailField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 155, 300, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 180, 300, 35);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(50, 230, 300, 40);
        loginBtn.setBackground(new Color(37, 99, 235));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> performLogin());
        panel.add(loginBtn);

        add(panel);
    }

    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            if (passwordEncoder.matches(password, user.getPassword())) {
                this.dispose();
                dashboardForm.init(user);
                dashboardForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }, () -> {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
