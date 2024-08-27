package com.drsanches.photobooth.app.notifier.service.web;

import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.notifier.data.fcm.FcmTokenDomainService;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Validated
public class FcmTokenWebService {

    @Autowired
    private FcmTokenDomainService fcmTokenDomainService;

    @Autowired
    private AuthInfo authInfo;

    public FcmTokenExpiresDto addToken(@Valid FcmTokenDto fcmTokenDto) {
        var userId = authInfo.getUserId();
        var fcmToken = fcmTokenDomainService.findByToken(fcmTokenDto.getFcmToken())
                .orElseGet(() -> fcmTokenDomainService.create(
                        userId,
                        fcmTokenDto.getFcmToken(),
                        Instant.now().plus(60, ChronoUnit.DAYS)
                ));
        return new FcmTokenExpiresDto(fcmToken.getExpires().toString());
    }
}
