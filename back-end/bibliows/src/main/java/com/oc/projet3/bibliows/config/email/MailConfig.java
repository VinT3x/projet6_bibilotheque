package com.oc.projet3.bibliows.config.email;

import java.util.Properties;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${app.mail.host}")
    String host;

    @Value("${app.mail.port}")
    int port;

    @Value("${app.mail.username}")
    String username;

    @Value("${app.mail.password}")
    String password;


    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mailWarnReservationTemplate.html.transport.protocol", "smtp");
        props.put("mailWarnReservationTemplate.html.smtp.auth", "true");
        props.put("mailWarnReservationTemplate.html.smtp.starttls.enable", "true");
        props.put("mailWarnReservationTemplate.html.debug", "true");

        return mailSender;
    }


    

}