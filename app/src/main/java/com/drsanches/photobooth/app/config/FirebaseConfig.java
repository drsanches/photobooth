package com.drsanches.photobooth.app.config;

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
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "application.notifications.push-enabled")
public class FirebaseConfig {

    private static final String APP_NAME = "PhotoBooth";

    @Value("${application.notifications.firebase-credentials-path}")
    private String firebaseCredentialsPath;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream resource = new ClassPathResource(firebaseCredentialsPath).getInputStream();
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(resource);
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, APP_NAME);
        return FirebaseMessaging.getInstance(app);
    }
}
