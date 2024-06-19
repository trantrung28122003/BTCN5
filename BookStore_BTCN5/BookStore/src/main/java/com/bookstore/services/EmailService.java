package com.bookstore.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.fromEmail}")
    private String fromEmail;


    public void sendVerificationCode(String toEmail, String subject, String verificationCode) throws MessagingException, IOException {
        // Đọc template email
        String emailTemplatePath = Paths.get("src/main/resources/templates/Email/EmailVerification.html").toString();
        String emailTemplate = new String(Files.readAllBytes(Paths.get(emailTemplatePath)));

        // Thay thế các placeholder trong template bằng giá trị thực tế
        String emailBody = emailTemplate.replace("[verificationCode]", verificationCode);

        // Cấu hình SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Tạo session với chứng thực
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Tạo tin nhắn email
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(subject);
        msg.setContent(emailBody, "text/html; charset=utf-8");

        // Gửi email
        Transport.send(msg);
        System.out.println("Email sent successfully.");
    }


}