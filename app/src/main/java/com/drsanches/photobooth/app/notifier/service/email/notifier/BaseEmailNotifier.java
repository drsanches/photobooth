package com.drsanches.photobooth.app.notifier.service.email.notifier;

import com.drsanches.photobooth.app.notifier.service.Notifier;
import com.drsanches.photobooth.app.notifier.service.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseEmailNotifier implements Notifier {

    @Autowired
    private EmailService emailService;

    protected void sendEmail(String email, String subject, String message) {
        emailService.sendHtmlMessage(email, subject, message);
    }
}
