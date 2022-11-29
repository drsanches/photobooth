package ru.drsanches.photobooth.auth.service.utils.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.common.exception.server.ServerError;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.email-sending-enabled", havingValue = "true")
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendHtmlMessage(String to, String subject, String message) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (MessagingException e) {
            throw new ServerError("Mail message initialization error", e);
        }
        emailSender.send(mimeMessage);
        log.info("Email message sent. Subject: {}, to: {}", subject, to);
    }
}
