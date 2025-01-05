package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.auth.exception.ForbiddenException;
import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.common.exception.BaseException;
import com.drsanches.photobooth.app.common.exception.dto.ErrorResponseDto;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.config.filter.TokenAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final Predicate<String> COOKIES_AUTH_URI = ((Predicate<String>) //TODO: Use something different?
            x -> x.matches("/ui/.*"))
            .or(x -> x.matches("/actuator.*"))
            .or(x -> x.matches("/h2-console.*"))
            .or(x -> x.matches("/swagger-ui.*"))
            .or(x -> x.matches("/v3/api-docs.*"));

    @Autowired
    private AuthIntegrationService authIntegrationService;
    @Autowired
    private AuthInfo authInfo;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(authenticationErrorHandler())
                        .accessDeniedHandler(forbiddenErrorHandler())
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(
                        new TokenAuthenticationFilter(authIntegrationService, authInfo, COOKIES_AUTH_URI),
                        UsernamePasswordAuthenticationFilter.class
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                match("GET", "/ui.*"),
                                match("GET", "/actuator/health.*"),
                                match("POST", "/api/v1/auth/account/?"),
                                match("GET", "/api/v1/auth/account/confirm/.*"),
                                match("POST", "/api/v1/auth/token/?"),
                                match("GET", "/api/v1/auth/token/refresh/?"),
                                match("POST", "/api/v1/auth/google/token/?"),
                                match("GET", "/api/v1/app/image/data/.*")
                        ).permitAll()
                        .requestMatchers(
                                match("/api/v1/admin/.*"),
                                match("/actuator.*"),
                                match("/h2-console.*"),
                                match("/swagger-ui.*"),
                                match("/v3/api-docs.*")
                        ).hasAuthority(Role.ADMIN.name())
                        .requestMatchers(
                                match("/api/.*")
                        ).authenticated()
                        .anyRequest().denyAll())
                .build();
    }

    private RequestMatcher match(String uriPattern) { //TODO: Remove
        return request -> request.getRequestURI().matches(uriPattern);
    }

    private RequestMatcher match(String method, String uriPattern) { //TODO: Remove
        return request -> request.getMethod().equals(method) && request.getRequestURI().matches(uriPattern);
    }

    private AuthenticationEntryPoint authenticationErrorHandler() {
        return (request, response, authException) ->
                writeResponse(
                        request,
                        response,
                        authException,
                        HttpStatus.UNAUTHORIZED,
                        new WrongTokenAuthException()
                );
    }

    private AccessDeniedHandler forbiddenErrorHandler() {
        return (request, response, authException) ->
                writeResponse(
                        request,
                        response,
                        authException,
                        HttpStatus.FORBIDDEN,
                        new ForbiddenException()
                );
    }

    private void writeResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception authException,
            HttpStatus responseStatus,
            BaseException responseException
    ) throws IOException {
        log.info(
                "Auth error: {}, endpoint: {}, uuid: {}",
                responseException.getMessage(),
                request.getMethod() + " " + request.getRequestURI(),
                responseException.getUuid(),
                authException
        );
        response.setStatus(responseStatus.value());
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().flush();
        response.getOutputStream().println(new ErrorResponseDto(responseException).toString());
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
