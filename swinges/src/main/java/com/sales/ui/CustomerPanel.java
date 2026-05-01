package com.sales.ui;

import com.sales.model.Customer;
import com.sales.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class CustomerPanel extends JPanel {

    private final CustomerRepository customerRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerPanel(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JLabel title = new JLabel("Customer Directory");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Phone", "Status"};
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
        List<Customer> customers = customerRepository.findAll();
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                "Active"
            });
        }
    }
}
