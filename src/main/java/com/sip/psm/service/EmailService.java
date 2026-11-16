package com.sip.psm.service;

import com.sip.psm.entity.User;
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
    private final String from;
    private final String adminEmail;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String from,
                        @Value("${app.admin.email}") String adminEmail) {
        this.mailSender = mailSender;
        this.from = from;
        this.adminEmail = adminEmail;
    }

    public void notifyAdminNewRegistration(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(adminEmail);
            message.setSubject("[PSM] Nouvelle inscription : " + user.getEmail());
            message.setText("""
                    Un nouvel utilisateur vient de s'inscrire sur Projects Manager.

                    Nom : %s
                    Prénom : %s
                    Email : %s
                    Téléphone : %s
                    Rôle attribué : COLLABORATEUR
                    """.formatted(user.getNom(), user.getPrenom(), user.getEmail(), user.getTel()));
            mailSender.send(message);
            log.info("Email de notification envoyé à {}", adminEmail);
        } catch (Exception e) {
            // L'échec de l'email ne doit jamais faire échouer l'inscription
            log.error("Échec de l'envoi de l'email à l'admin : {}", e.getMessage());
        }
    }
}