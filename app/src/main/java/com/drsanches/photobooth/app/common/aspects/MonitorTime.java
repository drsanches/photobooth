package com.drsanches.photobooth.app.common.aspects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Logs only methods of this class or interface.<br>
 * To log parent methods, they must be overridden.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorTime {
}
