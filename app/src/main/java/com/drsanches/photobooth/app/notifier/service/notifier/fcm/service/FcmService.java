package com.drsanches.photobooth.app.notifier.service.notifier.fcm.service;

import com.google.firebase.messaging.BatchResponse;

import java.util.List;

public interface FcmService {

    List<FcmResult> sendMessageWithImage(List<String> tokens, String title, String body, String imageId);

    record FcmResult(String fcmToken, boolean success) {}
}
