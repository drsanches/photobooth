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
        var publicUri = ((Predicate<String>)
                x -> x.matches("/actuator/health.*"))
                .or(x -> x.matches("/api/v1/auth/registration.*"))
                .or(x -> x.matches("/api/v1/auth/login.*"))
                .or(x -> x.matches("/api/v1/auth/confirm.*"))
                .or(x -> x.matches("/api/v1/auth/refreshToken.*"))
                .or(x -> x.matches("/api/v1/auth/google/token.*"))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID))
                .or(x -> x.matches("/api/v1/image/" + imageIdPattern))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + imageIdPattern + "/thumbnail.*"));
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(authIntegrationService, authInfo, publicUri));
        registrationBean.addUrlPatterns(
                "/actuator/*",
                "/h2-console/*",
                "/swagger-ui/*",
                "/api/v1/auth/*",
                "/api/v1/profile/*",
                "/api/v1/friends/*",
                "/api/v1/image/*",
                "/api/v1/notification/*"
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
        var imageIdPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        var publicUri = ((Predicate<String>)
                x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID))
                .or(x -> x.matches("/api/v1/image/" + imageIdPattern))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID + "/thumbnail.*"))
                .or(x -> x.matches("/api/v1/image/" + imageIdPattern + "/thumbnail.*"));
        FilterRegistrationBean<UserProfileSyncFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserProfileSyncFilter(
                authInfo,
                userProfileDomainService,
                publicUri
        ));
        registrationBean.addUrlPatterns(
                "/api/v1/profile/*",
                "/api/v1/friends/*",
                "/api/v1/image/*"
        );
        registrationBean.setOrder(4);
        return registrationBean;
    }
}