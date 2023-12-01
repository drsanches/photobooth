package com.drsanches.photobooth.app.notifier.email.notifier;

import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.notifier.Notifier;
import com.drsanches.photobooth.app.notifier.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseEmailNotifier implements Notifier {

    @Autowired
    private EmailService emailService;

    protected void sendEmail(List<String> emails, String subject, String message) {
        if (emails.isEmpty()) {
            throw new ServerError("Email list is empty");
        }
        for (String email: emails) {
            emailService.sendHtmlMessage(email, subject, message);
        }
    }
}
