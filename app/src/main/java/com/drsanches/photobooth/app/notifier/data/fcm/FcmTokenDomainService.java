package com.drsanches.photobooth.app.notifier.data.fcm;

import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FcmTokenDomainService {

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    //TODO: Refactor?
    public FcmToken getOrCreate(String userId, String token) {
        var existingFcmToken = fcmTokenRepository.findByToken(token);
        if (existingFcmToken.isPresent()) {
            log.info("FcmToken already exists. UserId: {}, fcmToken: {}", userId, existingFcmToken.get());
            return existingFcmToken.get();
        } else {
            var createdFcmToken = fcmTokenRepository.save(FcmToken.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .token(token)
                    .expires(Instant.now().plus(60, ChronoUnit.DAYS))
                    .build());
            log.info("FcmToken created: {}", createdFcmToken);
            return createdFcmToken;
        }
    }

    public List<FcmToken> findAllByUserId(String userId) {
        return fcmTokenRepository.findByUserId(userId);
    }

    public List<FcmToken> findAllExpired() {
        return fcmTokenRepository.findByExpiresLessThan(Instant.now());
    }

    //TODO: Use one db operation
    public void deleteAllByTokens(List<String> tokens) {
        var tokensToDelete = fcmTokenRepository.findByTokenIn(tokens);
        fcmTokenRepository.deleteAll(tokensToDelete);
        log.debug("FcmToken objects deleted: {}", tokensToDelete);
    }

    public void deleteAll(List<FcmToken> tokens) {
        fcmTokenRepository.deleteAll(tokens);
        log.debug("FcmToken objects deleted: {}", tokens);
    }
}
