package com.drsanches.photobooth.app.notifier.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NotificationService {

    @Autowired
    private List<Notifier> notifiers;

    public void notify(Action action, Map<String, String> params) {
        for (var notifier: notifiers) {
            if (notifier.isAcceptable(action)) {
                try {
                    notifier.notify(action, params);
                } catch (Exception e) {
                    log.error("Exception occurred during notification. Action: {}, params: {}", action, params, e);
                }
            }
        }
    }
}
