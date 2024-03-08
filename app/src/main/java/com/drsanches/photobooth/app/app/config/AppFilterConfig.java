package com.drsanches.photobooth.app.app.config;

import com.drsanches.photobooth.app.app.config.filter.UserProfileFilter;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class AppFilterConfig {

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Bean
    public FilterRegistrationBean<UserProfileFilter> userProfileFilter() {
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
        FilterRegistrationBean<UserProfileFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserProfileFilter(
                userInfo,
                userProfileDomainService,
                authIntegrationService,
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
