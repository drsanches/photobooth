package com.drsanches.photobooth.app.notifier.config;

import com.drsanches.photobooth.app.common.exception.ServerError;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "content.notifications")
public class NotificationContentProperties {

    //TODO: Separate to 2 different content classes

    public record EmailContent(String subject, String text) {}

    public record PushContent(String title, String body) {}

    private Map<Action, EmailContent> email;

    private Map<Action, PushContent> push;

    public void setEmail(Map<Action, EmailContent> email) {
        this.email = email;
    }

    public void setPush(Map<Action, PushContent> push) {
        this.push = push;
    }

    public EmailContent getEmailContent(Action action) {
        if (!email.containsKey(action)) {
            throw new ServerError("Configuration content.notifications.email does not contain " + action);
        }
        return email.get(action);
    }

    public PushContent getPushContent(Action action) {
        if (!push.containsKey(action)) {
            throw new ServerError("Configuration content.notifications.push does not contain " + action);
        }
        return push.get(action);
    }
}
