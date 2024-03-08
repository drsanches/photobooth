package com.drsanches.photobooth.app.notifier.service.web;

import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.notifier.data.fcm.FcmTokenDomainService;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class FcmTokenWebService {

    @Autowired
    private FcmTokenDomainService fcmTokenDomainService;

    @Autowired
    private AuthInfo authInfo;

    public FcmTokenExpiresDto addToken(@Valid FcmTokenDto fcmTokenDto) {
        var userId = authInfo.getUserId();
        var fcmToken = fcmTokenDomainService.getOrCreate(userId, fcmTokenDto.getFcmToken());
        log.info("New fcm token added. UserId: {}, fcmToken: {}", userId, fcmTokenDto.getFcmToken());
        return new FcmTokenExpiresDto(GregorianCalendarConvertor.convert(fcmToken.getExpires()));
    }
}
