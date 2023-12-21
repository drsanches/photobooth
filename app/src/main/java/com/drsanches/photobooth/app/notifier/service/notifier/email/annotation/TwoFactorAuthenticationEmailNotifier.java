package com.drsanches.photobooth.app.notifier.service.notifier.email.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Component
@ConditionalOnProperty(name = "application.notifications.email.2fa-enabled")
public @interface TwoFactorAuthenticationEmailNotifier {
}
