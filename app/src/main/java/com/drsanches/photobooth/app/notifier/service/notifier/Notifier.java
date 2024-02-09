package com.drsanches.photobooth.app.notifier.service.notifier;

import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import org.slf4j.Logger;

public interface Notifier {

    Logger getLogger();

    boolean isAcceptable(Action action);

    void notify(Action action, NotificationParams params);

    default void logAndNotify(Action action, NotificationParams params) {
        getLogger().info("Notification process started. Action: {}, params: {}", action, params);
        notify(action, params);
    }
}
