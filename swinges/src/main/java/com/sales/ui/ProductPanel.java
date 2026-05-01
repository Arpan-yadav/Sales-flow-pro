package com.sales.ui;

import com.sales.model.Product;
import com.sales.repository.ProductRepository;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class ProductPanel extends JPanel {

    private final ProductRepository productRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductPanel(ProductRepository productRepository) {
        this.productRepository = productRepository;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Category", "Price", "Stock", "Status"};
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
        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory() != null ? p.getCategory().getName() : "N/A",
                p.getPrice(),
                p.getStockQuantity(),
                p.isActive() ? "Active" : "Inactive"
            });
        }
    }
}
