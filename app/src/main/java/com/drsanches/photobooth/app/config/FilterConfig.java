package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.config.filter.UserProfileSyncFilter;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.auth.service.TokenService;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.config.filter.AdminFilter;
import com.drsanches.photobooth.app.config.filter.AuthFilter;
import com.drsanches.photobooth.app.config.filter.LogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class FilterConfig {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthInfo authInfo;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilter() {
        var imageIdPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        var publicEndpoint = ((Predicate<String>)
                x -> x.matches("GET /actuator/health.*"))
                .or(x -> x.matches("POST /api/v1/auth/account/?"))
                .or(x -> x.matches("GET /api/v1/auth/account/confirm/.*"))
                .or(x -> x.matches("POST /api/v1/auth/token/?"))
                .or(x -> x.matches("GET /api/v1/auth/token/refresh/?"))
                .or(x -> x.matches("POST /api/v1/auth/google/token/?"))
                .or(x -> x.matches("GET /api/v1/app/image/data/.*"));
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(authIntegrationService, authInfo, publicEndpoint));
        registrationBean.addUrlPatterns(
                "/actuator/*",
                "/h2-console/*",
                "/swagger-ui/*",
                "/api/v1/auth/*",
                "/api/v1/app/profile/*",
                "/api/v1/app/friends/*",
                "/api/v1/app/image/*",
                "/api/v1/notification/*",
                "/api/v1/admin/*"
        );
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter() {
        var publicUri = (Predicate<String>) x -> x.matches("/actuator/health.*");
        FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter(authInfo, publicUri));
        registrationBean.addUrlPatterns(
                "/api/v1/admin/*",
                "/actuator/*",
                "/h2-console/*",
                "/swagger-ui/*"
        );
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        var excludeUri = ((Predicate<String>)
                x -> x.matches("/ui/css.*"))
                .or(x -> x.matches("/ui/js.*"))
                .or(x -> x.matches("/swagger-ui/.*.js"))
                .or(x -> x.matches("/swagger-ui/.*.css"))
                .or(x -> x.matches("/swagger-ui/.*.map"))
                .or(x -> x.matches("/swagger-ui/.*.png"));
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(authInfo, excludeUri));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<UserProfileSyncFilter> userProfileSyncFilter() {
        Predicate<String> publicUri = x -> x.matches("/api/v1/app/image/data/.*");
        FilterRegistrationBean<UserProfileSyncFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserProfileSyncFilter(
                authInfo,
                userProfileDomainService,
                publicUri
        ));
        registrationBean.addUrlPatterns(
                "/api/v1/app/profile/*",
                "/api/v1/app/friends/*",
                "/api/v1/app/image/*"
        );
        registrationBean.setOrder(4);
        return registrationBean;
    }
}
