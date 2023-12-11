package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.data.email.EmailInfoDomainService;
import lombok.extern.slf4j.Slf4j;
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
    private EmailNotificationsContentProperties content;

    @Override
    public boolean isAcceptable(Action action) {
        return ACTIONS.contains(action);
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        log.info("Notification process started. Action: {}, params: {}", action, params);
        sendEmail(
                emailInfoDomainService.getByUserId(params.get("userId")).getEmail(),
                content.getSubject(action),
                String.format(content.getText(action), params.get("code"))
        );
    }
}