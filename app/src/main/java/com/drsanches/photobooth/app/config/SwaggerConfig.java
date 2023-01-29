package com.drsanches.photobooth.app.config;

import io.swagger.v3.oas.models.OpenAPI;
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
                .description("<font size=\"+1\">" +
                        "PhotoBooth API for developers<br><br>" +
                        "All admin info: <a href=\"/ui/index.html\">/ui/index.html</a>" +
                        "</font>"
                )
                .version("1.0");
    }
}
