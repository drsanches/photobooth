package com.drsanches.photobooth.app.auth.config;

import com.drsanches.photobooth.app.auth.config.filter.AuthFilter;
import com.drsanches.photobooth.app.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class AuthFilterConfig {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthInfo authInfo;

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilter() {
        var publicUri = ((Predicate<String>)
                x -> x.matches("/api/v1/auth/registration.*"))
                .or(x -> x.matches("/api/v1/auth/login.*"))
                .or(x -> x.matches("/api/v1/auth/confirm.*"))
                .or(x -> x.matches("/api/v1/auth/refreshToken.*"))
                .or(x -> x.matches("/api/v1/auth/google/token.*"));
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(tokenService, publicUri));
        registrationBean.addUrlPatterns("/api/v1/auth/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
