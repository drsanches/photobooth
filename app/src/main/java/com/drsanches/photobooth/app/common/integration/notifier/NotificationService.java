package com.drsanches.photobooth.app.common.integration.notifier;

import com.drsanches.photobooth.app.notifier.service.notifier.Action;

//TODO: Merge with NotifierIntegrationService?
public interface NotificationService {

    void notify(Action action, NotificationParams notificationParams);
}
