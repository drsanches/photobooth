package com.drsanches.photobooth.app.notifier.data.fcm;

import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FcmTokenDomainService {

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    public FcmToken create(String userId, String token, Instant expires) {
        var createdFcmToken = fcmTokenRepository.save(FcmToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .token(token)
                .expires(expires)
                .build());
        log.info("New FcmToken created: {}", createdFcmToken);
        return createdFcmToken;
    }

    public Optional<FcmToken> findByToken(String token) {
        return fcmTokenRepository.findByToken(token);
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
