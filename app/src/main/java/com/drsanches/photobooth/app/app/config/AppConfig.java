package com.drsanches.photobooth.app.app.config;

import com.drsanches.photobooth.app.app.config.filter.UserProfileFilter;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.service.AuthIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    UserProfileDomainService userProfileDomainService;

    @Autowired
    AuthIntegrationService authIntegrationService;

    @Bean
    public FilterRegistrationBean<UserProfileFilter> userProfileFilter() {
        FilterRegistrationBean<UserProfileFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserProfileFilter(userProfileDomainService, authIntegrationService));
        registrationBean.addUrlPatterns("/api/v1/profile/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }
}
