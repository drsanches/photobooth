package com.drsanches.photobooth.app.scheduler;

import com.drsanches.photobooth.app.common.token.data.Token;
import com.drsanches.photobooth.app.common.token.data.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.scheduler.expired-token-clean-task.enabled")
public class ExpiredTokenCleanTask {

    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(cron = "${application.scheduler.expired-token-clean-task.cron}")
    public void cleanExpiredTokens() {
        log.info("ExpiredTokenCleanTask started");
        List<Token> expired = tokenRepository.findByExpiresAtLessThan(new GregorianCalendar());
        tokenRepository.deleteAll(expired);
        log.info("Deleted {} expired tokens: {}", expired.size(), expired);
    }
}
