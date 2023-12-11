package com.drsanches.photobooth.app.notifier.service.notifier.fcm.notifier;

import com.drsanches.photobooth.app.notifier.data.fcm.FcmTokenDomainService;
import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import com.drsanches.photobooth.app.notifier.service.notifier.Notifier;
import com.drsanches.photobooth.app.notifier.service.notifier.fcm.service.FcmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public abstract class BaseFcmNotifier implements Notifier {

    @Autowired
    private FcmService fcmService;

    @Autowired
    private FcmTokenDomainService fcmTokenDomainService;

    protected void sendPush(String userId, String title, String body) {
        List<String> fcmTokens = fcmTokenDomainService.getByUserId(userId).stream()
                .map(FcmToken::getToken)
                .toList();
        List<FcmService.FcmResult> result = fcmService.sendMessage(fcmTokens, title, body);
        deleteFailedTokens(result, userId);
    }

    private void deleteFailedTokens(List<FcmService.FcmResult> fcmResults, String userId) {
        List<String> fcmTokensToDelete = fcmResults.stream()
                .filter(it -> !it.success())
                .map(FcmService.FcmResult::fcmToken)
                .toList();
        fcmTokenDomainService.deleteByTokens(fcmTokensToDelete);
        log.info("Deleted {} wrong fcm tokens. UserId: {}", fcmTokensToDelete.size(), userId);
    }
}