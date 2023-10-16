package com.drsanches.photobooth.app.notifier.email;

import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import com.drsanches.photobooth.app.notifier.Action;
import com.drsanches.photobooth.app.notifier.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailNotifier implements Notifier {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailNotificationsContentProperties content;

    @Override
    public void notify(Action action, Map<String, String> params) {
        switch (action) {
            case REGISTRATION_STARTED,
                    USERNAME_CHANGE_STARTED,
                    PASSWORD_CHANGE_STARTED,
                    EMAIL_CHANGE_STARTED,
                    DISABLE_STARTED -> send2FACode(action, params);
            case REGISTRATION_COMPLETED,
                    USERNAME_CHANGE_COMPLETED,
                    PASSWORD_CHANGE_COMPLETED,
                    EMAIL_CHANGE_COMPLETED,
                    DISABLE_COMPLETED -> sendCompleted(action, params);
        }
    }

    private void send2FACode(Action action, Map<String, String> params) {
        emailService.sendHtmlMessage(
                params.get("email"),
                content.getSubject(action),
                String.format(content.getText(action), params.get("code"))
        );
    }

    private void sendCompleted(Action action, Map<String, String> params) {
        emailService.sendHtmlMessage(
                params.get("email"),
                content.getSubject(action),
                content.getText(action)
        );
    }
}
