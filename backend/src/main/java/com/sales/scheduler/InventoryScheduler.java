package com.sales.scheduler;

import com.sales.model.Product;
import com.sales.model.User;
import com.sales.model.enums.Role;
import com.sales.repository.ProductRepository;
import com.sales.repository.UserRepository;
import com.sales.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryScheduler {

    private static final Logger log = LoggerFactory.getLogger(InventoryScheduler.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public InventoryScheduler(ProductRepository productRepository, UserRepository userRepository, EmailService emailService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkLowStock() {
        log.info("Running low stock check...");
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        
        if (lowStockProducts.isEmpty()) {
            log.info("No low stock products found.");
            return;
        }

        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN && u.isActive())
                .toList();

        for (Product p : lowStockProducts) {
            for (User admin : admins) {
                emailService.sendLowStockAlert(admin.getEmail(), p.getName(), p.getStockQuantity());
            }
        }
        log.info("Sent low stock alerts for {} products to {} admins.", lowStockProducts.size(), admins.size());
    }
}
