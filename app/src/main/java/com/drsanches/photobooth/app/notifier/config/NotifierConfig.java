package com.drsanches.photobooth.app.notifier.config;

import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.notifier.config.filter.NotifierFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotifierConfig {

    @Autowired
    private NotifierUserInfo notifierUserInfo;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Bean
    public FilterRegistrationBean<NotifierFilter> notifierFilter() {
        FilterRegistrationBean<NotifierFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new NotifierFilter(
                notifierUserInfo,
                authIntegrationService
        ));
        registrationBean.addUrlPatterns("/api/v1/notification/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }
}
