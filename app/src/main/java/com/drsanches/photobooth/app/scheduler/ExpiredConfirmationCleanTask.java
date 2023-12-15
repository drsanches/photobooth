package com.drsanches.photobooth.app.scheduler;

import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-confirmation-clean-task.enabled")
public class ExpiredConfirmationCleanTask {

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    @Scheduled(cron = "${application.scheduler.expired-confirmation-clean-task.cron}")
    public void cleanExpiredTokens() {
        log.info("ExpiredConfirmationCleanTask started");
        var expired = confirmationDomainService.getExpired();
        confirmationDomainService.deleteAll(expired);
        log.info("Deleted {} expired confirmations: {}", expired.size(), expired);
    }
}
