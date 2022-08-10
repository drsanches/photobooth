package ru.drsanches.photobooth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.drsanches.photobooth.common.utils.Initializer;

import java.util.List;

@SpringBootApplication
public class Application {

    private static List<Initializer> initializers;

    @Autowired
    private void setInitializers(List<Initializer> initializers) {
        Application.initializers = initializers;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        initializers.forEach(Initializer::initialize);
    }
}
