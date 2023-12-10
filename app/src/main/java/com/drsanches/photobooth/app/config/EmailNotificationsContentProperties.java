package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.notifier.service.Action;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "content.notifications.email")
public class EmailNotificationsContentProperties {

    private Map<String, String> subject;

    private Map<String, String> text;

    public void setSubject(Map<String, String> subject) {
        validateMapConsistency(subject);
        this.subject = subject;
    }

    public void setText(Map<String, String> text) {
        validateMapConsistency(text);
        this.text = text;
    }

    public String getSubject(Action action) {
        return subject.get(action.toString());
    }

    public String getText(Action action) {
        return text.get(action.toString());
    }

    private void validateMapConsistency(Map<String, String> map) {
        List<String> actions = Arrays.stream(Action.values())
                .map(Action::toString)
                .toList();

        if (map.keySet().size() != actions.size() || !map.keySet().containsAll(actions)) {
            throw ServerError.createWithMessage("Invalid content.notifications.email configuration");
        }
    }
}
