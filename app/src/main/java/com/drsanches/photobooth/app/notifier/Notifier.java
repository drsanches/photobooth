package com.drsanches.photobooth.app.notifier;

import java.util.Map;

public interface Notifier {

    boolean isAcceptable(Action action);

    void notify(Action action, Map<String, String> params);
}
