package com.drsanches.photobooth.app.scheduler;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.repository.ConfirmationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-confirmation-clean-task.enabled")
public class ExpiredConfirmationCleanTask {

    @Autowired
    private ConfirmationRepository confirmationRepository;

    @Scheduled(cron = "${application.scheduler.expired-confirmation-clean-task.cron}")
    public void cleanExpiredTokens() {
        log.info("ExpiredConfirmationCleanTask started");
        List<Confirmation> expired = confirmationRepository.findByExpiresAtLessThan(new GregorianCalendar());
        confirmationRepository.deleteAll(expired);
        log.info("Deleted {} expired confirmations: {}", expired.size(), expired);
    }
}
