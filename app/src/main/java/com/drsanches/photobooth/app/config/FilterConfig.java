package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.config.filter.UserProfileSyncFilter;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.config.filter.LogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class FilterConfig {

    @Autowired
    private AuthInfo authInfo;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

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
        registrationBean.setOrder(1);
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
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
