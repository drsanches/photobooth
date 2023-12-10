package com.drsanches.photobooth.app.notifier.service.notifier.fcm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.notifications.push-enabled", havingValue = "false")
public class FcmServiceStub implements FcmService {

    @Override
    public List<FcmResult> sendMessage(List<String> tokens, String title, String body) {
        log.info("Push was not sent for {} tokens", tokens.size());
        return tokens.stream()
                .map(it -> new FcmResult(it, true))
                .toList();
    }
}
