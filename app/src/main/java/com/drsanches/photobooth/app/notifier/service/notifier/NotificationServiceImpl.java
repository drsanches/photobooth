package com.drsanches.photobooth.app.notifier.service.notifier;

import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private List<Notifier> notifiers;

    public void notify(Action action, NotificationParams params) {
        for (var notifier: notifiers) {
            if (notifier.isAcceptable(action)) {
                try {
                    notifier.logAndNotify(action, params);
                } catch (Exception e) {
                    log.error("Exception occurred during notification. Action: {}, params: {}", action, params, e);
                }
            }
        }
    }
}
