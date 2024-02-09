package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.notifier.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.service.notifier.email.annotation.TwoFactorAuthenticationEmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

@Slf4j
@TwoFactorAuthenticationEmailNotifier
public class AuthConfirmationEmailNotifier extends BaseEmailNotifier {

    private final static Set<Action> ACTIONS = Set.of(
            Action.USERNAME_CHANGE_STARTED,
            Action.PASSWORD_CHANGE_STARTED,
            Action.EMAIL_CHANGE_STARTED,
            Action.DISABLE_STARTED
    );

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
        return ACTIONS.contains(action);
    }

    @Override
    public void notify(Action action, NotificationParams params) {
        var link = host + "/api/v1/auth/confirm/" + params.getCode(); //TODO: Use const for path
        sendEmail(
                getEmail(params.getUserId()),
                content.getEmailContent(action).subject(),
                String.format(content.getEmailContent(action).text(), link)
        );
    }
}
