package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DisableCompletedEmailNotifier extends BaseEmailNotifier {

    @Autowired
    private NotificationContentProperties content;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.DISABLE_COMPLETED;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        sendEmail(
                params.get("email"),
                content.getEmailContent(action).subject(),
                content.getEmailContent(action).text()
        );
    }
}
