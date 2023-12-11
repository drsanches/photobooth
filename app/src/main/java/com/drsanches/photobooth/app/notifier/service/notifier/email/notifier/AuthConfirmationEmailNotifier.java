package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.data.email.EmailInfoDomainService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class AuthConfirmationEmailNotifier extends BaseEmailNotifier {

    private final static Set<Action> ACTIONS = Set.of(
            Action.USERNAME_CHANGE_STARTED,
            Action.PASSWORD_CHANGE_STARTED,
            Action.EMAIL_CHANGE_STARTED,
            Action.DISABLE_STARTED
    );

    @Autowired
    private EmailInfoDomainService emailInfoDomainService;

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
    public void notify(Action action, Map<String, String> params) {
        sendEmail(
                emailInfoDomainService.getByUserId(params.get("userId")).getEmail(),
                content.getEmailContent(action).subject(),
                String.format(content.getEmailContent(action).text(), params.get("code"))
        );
    }
}
