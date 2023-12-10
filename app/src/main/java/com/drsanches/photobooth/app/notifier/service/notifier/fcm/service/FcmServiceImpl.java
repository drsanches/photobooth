package com.drsanches.photobooth.app.notifier.service.notifier.fcm.service;

import com.drsanches.photobooth.app.notifier.exception.NotificationException;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "application.notifications.push-enabled")
public class FcmServiceImpl implements FcmService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Override
    public List<FcmResult> sendMessage(List<String> tokens, String title, String body) {
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        //TODO: Add image
                        .build())
                .build();
        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(msg);
            log.info("Push sent for {} tokens", tokens.size());
            return map(tokens, response);
        } catch (FirebaseMessagingException e) {
            throw new NotificationException("Push notification error", e);
        }
    }

    private List<FcmResult> map(List<String> tokens, BatchResponse batchResponse) {
        List<SendResponse> responses = batchResponse.getResponses();
        if (responses.size() != tokens.size()) {
            throw new NotificationException("Fcm response size does not correspond to the number of tokens sent");
        }
        List<FcmResult> results = new LinkedList<>();
        for (int i = 0; i < responses.size(); i++) {
            results.add(new FcmResult(tokens.get(i), responses.get(i).isSuccessful()));
        }
        return results;
    }
}
