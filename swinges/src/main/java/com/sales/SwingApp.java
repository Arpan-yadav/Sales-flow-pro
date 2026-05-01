package com.sales;

import com.sales.ui.LoginForm;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class SwingApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SwingApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        EventQueue.invokeLater(() -> {
            try {
                // Set look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            LoginForm loginForm = context.getBean(LoginForm.class);
            loginForm.setVisible(true);
        });
    }
}
