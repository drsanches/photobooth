package com.drsanches.photobooth.app.notifier.service.notifier.fcm.notifier;

import com.drsanches.photobooth.app.notifier.config.NotificationContentProperties;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ImageFcmNotifier extends BaseFcmNotifier {

    @Autowired
    private NotificationContentProperties content;

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.IMAGE_SENT;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        var imageId = params.get("imageId");
        for (var userId: params.get("toUsers").split(",")) {
            sendPushWithImage(
                    userId,
                    content.getPushContent(action).title(),
                    content.getPushContent(action).body(),
                    imageId
            );
        }
    }
}
