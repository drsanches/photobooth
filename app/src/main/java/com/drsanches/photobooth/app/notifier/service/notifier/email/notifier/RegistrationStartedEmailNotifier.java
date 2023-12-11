package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RegistrationStartedEmailNotifier extends BaseEmailNotifier {

    @Autowired
    private EmailNotificationsContentProperties content;

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.REGISTRATION_STARTED;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        log.info("Notification process started. Action: {}, params: {}", action, params);
        sendEmail(
                params.get("email"),
                content.getSubject(action),
                String.format(content.getText(action), params.get("code"))
        );
    }
}