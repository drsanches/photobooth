package com.drsanches.photobooth.app.notifier.service.web;

import com.drsanches.photobooth.app.common.token.UserInfo;
import com.drsanches.photobooth.app.notifier.data.fcm.FcmTokenDomainService;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
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
    private UserInfo userInfo;

    public void addToken(@Valid FcmTokenDto fcmTokenDto) {
        var userId = userInfo.getUserId();
        fcmTokenDomainService.create(userId, fcmTokenDto.getFcmToken());
        log.info("New fcm token added. UserId: {}, fcmToken: {}", userId, fcmTokenDto.getFcmToken());
    }
}
