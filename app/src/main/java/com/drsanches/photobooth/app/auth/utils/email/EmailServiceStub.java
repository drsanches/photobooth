package com.drsanches.photobooth.app.auth.utils.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.email-notifications.email-sending-enabled", havingValue = "false")
public class EmailServiceStub implements EmailService {

    public void sendHtmlMessage(String to, String subject, String message) {
        log.info("Email message was not send. Subject: {}, to: {}", subject, to);
    }
}
