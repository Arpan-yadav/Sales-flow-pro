package com.sales.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@sales.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLowStockAlert(String to, String productName, int currentStock) {
        String subject = "LOW STOCK ALERT: " + productName;
        String body = String.format(
            "Hello,\n\nThe product '%s' is running low on stock.\nCurrent Quantity: %d\n\nPlease restock soon.",
            productName, currentStock
        );
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
