package com.drsanches.photobooth.app.notifier.data.fcm;

import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FcmTokenDomainService {

    private static final int CALENDAR_FIELD = Calendar.MONTH;
    private static final int EXPIRES_CALENDAR_VALUE = 2;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    public void create(String userId, String token) {
        var expires = new GregorianCalendar();
        expires.add(CALENDAR_FIELD, EXPIRES_CALENDAR_VALUE);
        if (!fcmTokenRepository.existsByToken(token)) {
            var fcmToken = fcmTokenRepository.save(FcmToken.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .token(token)
                    .expires(expires)
                    .build());
            log.debug("FcmToken created. FcmToken: {}", fcmToken);
        } else {
            log.debug("FcmToken already exists. UserId: {}", userId);
        }
    }

    public List<FcmToken> getByUserId(String userId) {
        return fcmTokenRepository.findByUserId(userId);
    }

    public void deleteByTokens(List<String> tokens) {
        var deletedTokens = fcmTokenRepository.deleteByTokenIn(tokens);
        log.debug("FcmToken objects deleted. FcmTokenList: {}", deletedTokens);
    }
}
