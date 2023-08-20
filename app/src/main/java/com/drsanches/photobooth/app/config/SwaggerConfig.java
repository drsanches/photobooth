package com.drsanches.photobooth.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("PhotoBooth developer API")
                .description("PhotoBooth API for developers")
                .contact(new Contact()
                        .name("Admin page")
                        .url("/ui/index.html"))
                .version("1.0");
    }
}
