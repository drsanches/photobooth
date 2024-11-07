package com.drsanches.photobooth.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement())
                .components(components())
                .info(apiInfo());
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("Auth token", new SecurityScheme()
                        .description(
                                "Can be obtained from the " +
                                "<a href=\"/ui/index.html#/auth-info\" target=\"_blank\">admin panel</a><br>"
                        )
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .scheme("bearer")
                );
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
