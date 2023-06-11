package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.config.filter.LogFilter;
import com.drsanches.photobooth.app.config.filter.TokenFilter;
import com.drsanches.photobooth.app.config.filter.AdminFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.function.Predicate;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String IMAGE_ID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private final Predicate<String> PUBLIC_URI = ((Predicate<String>)
            x -> x.matches("/api/v1/auth/registration.*"))
            .or(x -> x.matches("/api/v1/auth/login.*"))
            .or(x -> x.matches("/api/v1/auth/refreshToken.*"))
            .or(x -> x.matches("/api/v1/auth/google/token.*"))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID))
            .or(x -> x.matches("/api/v1/image/" + IMAGE_ID_PATTERN))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.DEFAULT_AVATAR_ID + "/thumbnail.*"))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.NO_PHOTO_IMAGE_ID + "/thumbnail.*"))
            .or(x -> x.matches("/api/v1/image/" + ImageConsts.DELETED_AVATAR_ID + "/thumbnail.*"))
            .or(x -> x.matches("/api/v1/image/" + IMAGE_ID_PATTERN + "/thumbnail.*"))
            .or(x -> x.matches("/actuator/health.*"))
            .or(x -> x.matches("/ui.*"));

    private final Predicate<String> ADMIN_URI = ((Predicate<String>)
            x -> x.matches("/h2-console.*"))
            .or(x -> x.matches("/swagger-ui.html.*"))
            .or(((Predicate<String>) x -> x.matches("/actuator.*"))
                    .and(Predicate.not(x -> x.matches("/actuator/health.*"))));

    private final Predicate<String> EXCLUDE_LOG_URI = ((Predicate<String>)
            x -> x.matches("/ui/css.*"))
            .or(x -> x.matches("/ui/js.*"))
            .or(x -> x.matches("/swagger-ui/.*.js"))
            .or(x -> x.matches("/swagger-ui/.*.css"))
            .or(x -> x.matches("/swagger-ui/.*.map"))
            .or(x -> x.matches("/swagger-ui/.*.png"));

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new TokenFilter(tokenService, PUBLIC_URI), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AdminFilter(tokenSupplier, ADMIN_URI), TokenFilter.class);
        http.addFilterAfter(new LogFilter(tokenSupplier, EXCLUDE_LOG_URI), AdminFilter.class);
        http.cors().and().csrf().disable()
                .headers().frameOptions().disable()
                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN"))
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
