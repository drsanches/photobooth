package com.drsanches.photobooth.app.notifier.email.notifier;

import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import com.drsanches.photobooth.app.notifier.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DisableCompletedEmailNotifier extends BaseEmailNotifier {

    @Autowired
    private EmailNotificationsContentProperties content;

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.DISABLE_COMPLETED;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        log.info("Notification process started. Action: {}, params: {}", action, params);
        sendEmail(
                List.of(params.get("email")),
                content.getSubject(action),
                content.getText(action)
        );
    }
}
