package com.drsanches.photobooth.app.notifier.service.notifier;

import org.slf4j.Logger;

import java.util.Map;

public interface Notifier {

    Logger getLogger();

    boolean isAcceptable(Action action);

    void notify(Action action, Map<String, String> params);

    default void logAndNotify(Action action, Map<String, String> params) {
        getLogger().info("Notification process started. Action: {}, params: {}", action, params);
        notify(action, params);
    }
}
