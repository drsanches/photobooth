package com.drsanches.photobooth.app.auth.scheduler;

import com.drsanches.photobooth.app.auth.data.token.TokenDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-token-cleanup-task.enabled")
public class ExpiredTokenCleanupTask {

    @Autowired
    private TokenDomainService tokenDomainService;

    @Scheduled(cron = "${application.scheduler.expired-token-cleanup-task.cron}")
    public void cleanup() {
        log.info("ExpiredTokenCleanupTask started");
        var expired = tokenDomainService.findAllExpired();
        if (!expired.isEmpty()) {
            tokenDomainService.deleteAll(expired);
        }
        log.info("Deleted {} expired tokens: {}", expired.size(), expired);
    }
}
