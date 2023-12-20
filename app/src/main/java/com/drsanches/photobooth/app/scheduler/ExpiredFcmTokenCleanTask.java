package com.drsanches.photobooth.app.scheduler;

import com.drsanches.photobooth.app.notifier.data.fcm.FcmTokenDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-fcm-token-clean-task.enabled")
public class ExpiredFcmTokenCleanTask {

    @Autowired
    private FcmTokenDomainService fcmTokenDomainService;

    @Scheduled(cron = "${application.scheduler.expired-token-clean-task.cron}")
    public void cleanExpiredTokens() {
        log.info("ExpiredFcmTokenCleanTask started");
        var expired = fcmTokenDomainService.getExpired();
        if (!expired.isEmpty()) {
            fcmTokenDomainService.deleteAll(expired);
        }
        log.info("Deleted {} expired fcmTokens: {}", expired.size(), expired);
    }
}
