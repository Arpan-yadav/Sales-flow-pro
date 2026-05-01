package com.sales.ui;

import com.sales.model.User;
import com.sales.service.ReportService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

@Component
public class DashboardForm extends JFrame {

    private final ReportService reportService;
    private final ProductPanel productPanel;
    private final OrderPanel orderPanel;
    private final CustomerPanel customerPanel;
    
    private User currentUser;
    private JPanel mainContainer;
    private CardLayout cardLayout;

    public DashboardForm(ReportService reportService, ProductPanel productPanel, OrderPanel orderPanel, CustomerPanel customerPanel) {
        this.reportService = reportService;
        this.productPanel = productPanel;
        this.orderPanel = orderPanel;
        this.customerPanel = customerPanel;

        setTitle("SalesFlow Pro - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    public void init(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("SalesFlow Pro");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Inter", Font.BOLD, 20));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.add(logo);

        addSidebarButton(sidebar, "Dashboard", "DASH");
        addSidebarButton(sidebar, "Products", "PROD");
        addSidebarButton(sidebar, "Orders", "ORD");
        addSidebarButton(sidebar, "Customers", "CUST");
        
        sidebar.add(Box.createVerticalGlue());
        
        JLabel userLabel = new JLabel("User: " + currentUser.getName());
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sidebar.add(userLabel);

        // Content Area
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createDashboardPanel(), "DASH");
        mainContainer.add(productPanel, "PROD");
        mainContainer.add(orderPanel, "ORD");
        mainContainer.add(customerPanel, "CUST");

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainContainer, BorderLayout.CENTER);
    }

    private void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(15, 23, 42));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> cardLayout.show(mainContainer, cardName));
        sidebar.add(btn);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Map<String, Object> stats = reportService.getDashboard();

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 20));
        statsGrid.setBackground(Color.WHITE);

        statsGrid.add(createStatCard("Revenue Today", "₹" + stats.get("revenueToday")));
        statsGrid.add(createStatCard("Total Orders", String.valueOf(stats.get("ordersTotal"))));
        statsGrid.add(createStatCard("Customers", String.valueOf(stats.get("customersTotal"))));
        statsGrid.add(createStatCard("Low Stock", String.valueOf(stats.get("lowStockCount"))));

        panel.add(new JLabel("Welcome Back, " + currentUser.getName(), SwingConstants.LEFT) {{
            setFont(new Font("Inter", Font.BOLD, 24));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        }}, BorderLayout.NORTH);
        
        panel.add(statsGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel l = new JLabel(label);
        l.setForeground(Color.GRAY);
        l.setFont(new Font("Inter", Font.PLAIN, 12));
        
        JLabel v = new JLabel(value);
        v.setFont(new Font("Inter", Font.BOLD, 24));
        v.setForeground(new Color(15, 23, 42));

        card.add(l);
        card.add(Box.createVerticalStrut(10));
        card.add(v);
        return card;
    }
}
