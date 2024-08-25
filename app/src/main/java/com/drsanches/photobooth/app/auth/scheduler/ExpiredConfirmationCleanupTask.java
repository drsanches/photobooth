package com.drsanches.photobooth.app.auth.scheduler;

import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-confirmation-cleanup-task.enabled")
public class ExpiredConfirmationCleanupTask {

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    @Scheduled(cron = "${application.scheduler.expired-confirmation-cleanup-task.cron}")
    public void cleanup() {
        log.info("ExpiredConfirmationCleanupTask started");
        var expired = confirmationDomainService.findAllExpired();
        if (!expired.isEmpty()) {
            confirmationDomainService.deleteAll(expired);
        }
        log.info("Deleted {} expired confirmations: {}", expired.size(), expired);
    }
}
