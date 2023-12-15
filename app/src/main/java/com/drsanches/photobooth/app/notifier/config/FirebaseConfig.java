package com.drsanches.photobooth.app.notifier.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "application.notifications.push-enabled")
public class FirebaseConfig {

    private static final String APP_NAME = "PhotoBooth";

    @Value("${application.notifications.firebase-credentials-path}")
    private String firebaseCredentialsPath;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        var resource = new ClassPathResource(firebaseCredentialsPath).getInputStream();
        var googleCredentials = GoogleCredentials.fromStream(resource);
        var firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        var app = FirebaseApp.initializeApp(firebaseOptions, APP_NAME);
        return FirebaseMessaging.getInstance(app);
    }
}
