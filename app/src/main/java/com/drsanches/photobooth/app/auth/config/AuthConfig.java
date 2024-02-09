package com.drsanches.photobooth.app.auth.config;

import com.drsanches.photobooth.app.auth.config.filter.AdminFilter;
import com.drsanches.photobooth.app.auth.config.filter.TokenFilter;
import com.drsanches.photobooth.app.auth.service.TokenService;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class AuthConfig {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthInfo authInfo;

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilter() {
        var publicUri = ((Predicate<String>)
                x -> x.matches("/api/v1/auth/registration.*"))
                .or(x -> x.matches("/api/v1/auth/login.*"))
                .or(x -> x.matches("/api/v1/auth/confirm.*"))
                .or(x -> x.matches("/api/v1/auth/refreshToken.*"))
                .or(x -> x.matches("/api/v1/auth/google/token.*"))
                .or(x -> x.matches("/actuator/health.*"));
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(tokenService, publicUri));
        registrationBean.addUrlPatterns(
                "/api/v1/auth/*",
                "/actuator/*",
                "/h2-console/*",
                "/swagger-ui/*"
        );
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter() {
        var adminUri = ((Predicate<String>)
                x -> x.matches("/h2-console.*"))
                .or(x -> x.matches("/swagger-ui.html.*"))
                .or(((Predicate<String>) x -> x.matches("/actuator.*"))
                        .and(Predicate.not(x -> x.matches("/actuator/health.*"))));
        FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter(authInfo, adminUri));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
