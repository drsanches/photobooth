package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.notifier.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.service.notifier.email.annotation.TwoFactorAuthenticationEmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@TwoFactorAuthenticationEmailNotifier
public class RegistrationStartedEmailNotifier extends BaseEmailNotifier {

    @Autowired
    private NotificationContentProperties content;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.REGISTRATION_STARTED;
    }

    @Override
    public void notify(Action action, NotificationParams params) {
        sendEmail(
                params.getEmail(),
                content.getEmailContent(action).subject(),
                String.format(content.getEmailContent(action).text(), params.getConfirmationLink())
        );
    }
}
