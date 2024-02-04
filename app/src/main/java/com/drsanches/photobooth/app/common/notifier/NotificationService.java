package com.drsanches.photobooth.app.common.notifier;

import com.drsanches.photobooth.app.notifier.service.notifier.Action;

public interface NotificationService {

    void notify(Action action, NotificationParams notificationParams);
}
