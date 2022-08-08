package ru.drsanches.photobooth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.drsanches.photobooth.app.service.utils.DefaultImageInitializer;

@SpringBootApplication
public class Application {

    private static DefaultImageInitializer defaultImageInitializer;

    @Autowired
    private void setDefaultImageInitializer(DefaultImageInitializer defaultImageInitializer) {
        Application.defaultImageInitializer = defaultImageInitializer;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        defaultImageInitializer.initialize();
    }
}
