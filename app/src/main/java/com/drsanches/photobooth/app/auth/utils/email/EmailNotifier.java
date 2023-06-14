package com.drsanches.photobooth.app.auth.utils.email;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailNotificationsContentProperties emailNotificationsContentProperties;

    public void sendCode(String code, String email, Operation operation) {
        emailService.sendHtmlMessage(
                email,
                emailNotificationsContentProperties.getConfirmSubject(operation),
                "Code: " + code
        );
    }

    public void sendSuccessNotification(String email, Operation operation) {
        emailService.sendHtmlMessage(
                email,
                emailNotificationsContentProperties.getSuccessSubject(operation),
                emailNotificationsContentProperties.getSuccessSubject(operation)
        );
    }
}
