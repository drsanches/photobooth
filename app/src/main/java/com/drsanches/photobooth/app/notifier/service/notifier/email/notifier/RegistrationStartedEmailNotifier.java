package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.notifier.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.service.notifier.email.annotation.TwoFactorAuthenticationEmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Slf4j
@TwoFactorAuthenticationEmailNotifier
public class RegistrationStartedEmailNotifier extends BaseEmailNotifier {

    @Autowired
    private NotificationContentProperties content;

    @Value("${application.address}")
    private String host;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.REGISTRATION_STARTED;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        var link = host + "/api/v1/auth/confirm/" + params.get("code"); //TODO: Use const for path
        sendEmail(
                params.get("email"),
                content.getEmailContent(action).subject(),
                String.format(content.getEmailContent(action).text(), link)
        );
    }
}
