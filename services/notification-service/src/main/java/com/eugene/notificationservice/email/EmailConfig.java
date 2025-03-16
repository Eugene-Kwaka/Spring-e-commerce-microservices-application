package com.eugene.notificationservice.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Configuration
public class EmailConfig {

    // @Value annotations inject values from application.properties/yml into these fields
    // Gets the mail server host (e.g., "localhost" for MailDev in your case)
    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    // This method will be called to create the JavaMailSender instance
    @Bean
    public JavaMailSender javaMailSender() {
        // Creates a new implementation of JavaMailSender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Sets the SMTP server host (e.g., localhost)
        mailSender.setHost(mailHost);

        // Sets the SMTP server port (e.g., 1025)
        mailSender.setPort(mailPort);

        // Sets the username for SMTP authentication
        mailSender.setUsername(mailUsername);

        // Sets the password for SMTP authentication
        mailSender.setPassword(mailPassword);

        // Creates a Properties object to hold additional mail settings
        Properties props = mailSender.getJavaMailProperties();

        // Enable SMTP authentication
        props.put("mail.smtp.auth", "true");

        // Enable STARTTLS (Transport Layer Security) - encrypts the connection
        props.put("mail.smtp.starttls.enable", "true");

        // Socket read timeout value in milliseconds
        props.put("mail.smtp.timeout", "3000");

        // Socket connection timeout value in milliseconds
        props.put("mail.smtp.connectiontimeout", "5000");

        // Socket write timeout value in milliseconds
        props.put("mail.smtp.writetimeout", "5000");

        // Returns the configured JavaMailSender instance
        return mailSender;
    }
}