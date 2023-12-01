package com.drsanches.photobooth.app.notifier.email.notifier;

import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import com.drsanches.photobooth.app.notifier.Action;
import com.drsanches.photobooth.app.notifier.data.NotificationInfoDomainService;
import com.drsanches.photobooth.app.notifier.data.model.NotificationInfo;
import com.drsanches.photobooth.app.notifier.data.model.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class CommonEmailNotifier extends BaseEmailNotifier {

    private final static Set<Action> ACTIONS = Set.of(
            Action.REGISTRATION_COMPLETED,
            Action.USERNAME_CHANGE_COMPLETED,
            Action.PASSWORD_CHANGE_COMPLETED,
            Action.EMAIL_CHANGE_COMPLETED
    );

    @Autowired
    private NotificationInfoDomainService notificationInfoDomainService;

    @Autowired
    private EmailNotificationsContentProperties content;

    @Override
    public boolean isAcceptable(Action action) {
        return ACTIONS.contains(action);
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        log.info("Notification process started. Action: {}, params: {}", action, params);
        List<String> emails = notificationInfoDomainService
                .getByUserIdAndType(params.get("userId"), NotificationType.EMAIL)
                .stream()
                .map(NotificationInfo::getTarget)
                .toList();
        sendEmail(
                emails,
                content.getSubject(action),
                content.getText(action)
        );
    }
}
