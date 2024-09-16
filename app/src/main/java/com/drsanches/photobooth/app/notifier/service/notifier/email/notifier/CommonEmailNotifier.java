package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.notifier.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.service.notifier.email.annotation.InfoEmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Slf4j
@InfoEmailNotifier
public class CommonEmailNotifier extends BaseEmailNotifier {

    private final static Set<Action> ACTIONS = Set.of(
            Action.REGISTRATION_COMPLETED,
            Action.USERNAME_CHANGE_COMPLETED,
            Action.PASSWORD_CHANGE_COMPLETED,
            Action.EMAIL_CHANGE_COMPLETED
    );

    @Autowired
    private NotificationContentProperties content;

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
        sendEmail(
                params.getEmail(),
                content.getEmailContent(action).subject(),
                content.getEmailContent(action).text()
        );
    }
}
