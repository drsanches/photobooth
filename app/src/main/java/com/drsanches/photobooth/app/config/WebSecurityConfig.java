package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.token.AuthInfo;
import com.drsanches.photobooth.app.config.filter.LogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.function.Predicate;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private AuthInfo authInfo; //TODO: Remove

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        var excludeLogUri = ((Predicate<String>)
                x -> x.matches("/ui/css.*"))
                .or(x -> x.matches("/ui/js.*"))
                .or(x -> x.matches("/swagger-ui/.*.js"))
                .or(x -> x.matches("/swagger-ui/.*.css"))
                .or(x -> x.matches("/swagger-ui/.*.map"))
                .or(x -> x.matches("/swagger-ui/.*.png"));
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(authInfo, excludeLogUri));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN")))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        var corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
