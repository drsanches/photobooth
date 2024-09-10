package com.drsanches.photobooth.app.notifier.service.notifier.email.service;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.exception.ServerError;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Slf4j
@Component
@MonitorTime
@ConditionalOnProperty(name = "application.notifications.email.enabled")
public class EmailServiceImpl implements EmailService {

    //TODO
    private static final Predicate<String> TEST_ADDRESS = x -> x.matches(".*@example\\.com");

    @Autowired
    private JavaMailSender emailSender;

    public void sendHtmlMessage(String to, String subject, String message) {
        if (TEST_ADDRESS.test(to)) {
            log.info("Test address {}, ignoring", to);
            return;
        }
        var mimeMessage = emailSender.createMimeMessage();
        try {
            var helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (MessagingException e) {
            throw new ServerError("Mail message initialization error", e);
        }
        emailSender.send(mimeMessage);
        log.info("Email message sent. Email: {}", to);
    }
}
