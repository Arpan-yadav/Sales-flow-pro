package com.sales;

import com.sales.model.User;
import com.sales.model.enums.Role;
import com.sales.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class SalesManagementApplication {

    private static final Logger log = LoggerFactory.getLogger(SalesManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SalesManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@sales.com").isEmpty()) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail("admin@sales.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                userRepository.save(admin);
                log.info("✅ Default admin seeded: admin@sales.com / Admin@123");
            }
        };
    }
}
