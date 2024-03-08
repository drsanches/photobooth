package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.config.filter.AdminFilter;
import com.drsanches.photobooth.app.config.filter.LogFilter;
import com.drsanches.photobooth.app.config.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Predicate;

@Configuration
public class CommonFilterConfig {

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilter() {
        var excludeUri = ((Predicate<String>)
                x -> x.matches("/ui/css.*"))
                .or(x -> x.matches("/ui/js.*"))
                .or(x -> x.matches("/swagger-ui/.*.js"))
                .or(x -> x.matches("/swagger-ui/.*.css"))
                .or(x -> x.matches("/swagger-ui/.*.map"))
                .or(x -> x.matches("/swagger-ui/.*.png"));
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(authIntegrationService, excludeUri));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
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
        registrationBean.setFilter(new LogFilter(excludeUri));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    //TODO: Move to common
    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilter() {
        var publicUri = (Predicate<String>) x -> x.matches("/actuator/health.*");
        FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter(publicUri));
        registrationBean.addUrlPatterns(
                "/actuator/*",
                "/h2-console/*",
                "/swagger-ui/*"
        );
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
