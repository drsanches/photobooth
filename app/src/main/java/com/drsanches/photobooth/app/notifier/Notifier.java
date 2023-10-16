package com.drsanches.photobooth.app.notifier;

import java.util.Map;

public interface Notifier {

    void notify(Action action, Map<String, String> params);
}
