package com.sales.ui;

import com.sales.model.Order;
import com.sales.repository.OrderRepository;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class OrderPanel extends JPanel {

    private final OrderRepository orderRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    public OrderPanel(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JLabel title = new JLabel("Order History");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Customer", "Date", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<Order> orders = orderRepository.findAll();
        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                o.getId(),
                o.getCustomer() != null ? o.getCustomer().getName() : "Unknown",
                o.getCreatedAt(),
                "₹" + o.getTotal(),
                o.getStatus()
            });
        }
    }
}
