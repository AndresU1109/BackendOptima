package com.hrks.OptimaStock.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username:noreply@optimastock.com}")
    private String fromEmail;

    @Async
    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachmentData,
            String attachmentName) {
        logger.info("Iniciando envío de correo a: {}", to);
        try {
            if (to == null || subject == null || text == null) {
                logger.error("Datos de correo incompletos. No se puede enviar. To: {}, Subject: {}", to, subject);
                return;
            }

            MimeMessage message = emailSender.createMimeMessage();
            // Multipart mode
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail != null ? fromEmail : "noreply@optimastock.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (attachmentData != null && attachmentData.length > 0 && attachmentName != null) {
                helper.addAttachment(attachmentName, new ByteArrayResource(attachmentData));
            }

            emailSender.send(message);
            logger.info("Email enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            logger.error("Error enviando email a {}: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado enviando email a {}: {}", to, e.getMessage(), e);
        }
    }
}
