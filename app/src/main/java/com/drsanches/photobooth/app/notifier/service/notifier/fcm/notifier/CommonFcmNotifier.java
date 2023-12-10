package com.drsanches.photobooth.app.notifier.service.notifier.fcm.notifier;

import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommonFcmNotifier extends BaseFcmNotifier {

    @Override
    public boolean isAcceptable(Action action) {
        return action == Action.IMAGE_SENT;
    }

    @Override
    public void notify(Action action, Map<String, String> params) {
        for (String userId: params.get("toUsers").split(",")) {
            sendPush(userId, "New photo", "You've got a new photo, let's take a look at it!"); //TODO: Use resources for content
        }
    }
}
