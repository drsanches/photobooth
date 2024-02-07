package com.drsanches.photobooth.app.auth.config.filter;

import com.drsanches.photobooth.app.auth.exception.AuthException;
import com.drsanches.photobooth.app.common.token.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class TokenFilter extends GenericFilterBean {

    private final static String AUTHORIZATION = "Authorization";

    private final TokenService tokenService;

    private final Predicate<String> excludeUri;

    public TokenFilter(TokenService tokenService, Predicate<String> excludeUri) {
        this.tokenService = tokenService;
        this.excludeUri = excludeUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var token = getAccessTokenFromRequest(httpRequest);
        var uri = httpRequest.getRequestURI();
        try {
            //TODO: Refactor
            var userInfo = tokenService.validate(token);
            request.setAttribute("userId", userInfo.getUserId());
        } catch (AuthException e) {
            if (!excludeUri.test(uri)) {
                log.info("Wrong token for uri. Uri: {}", uri, e);
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                httpResponse.getOutputStream().flush();
                httpResponse.getOutputStream().println(e.getMessage());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Nullable
    private String getAccessTokenFromRequest(HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader(AUTHORIZATION);
        if (token == null) {
            token = getAccessTokenFromCookies(httpRequest.getCookies());
        }
        return token;
    }

    @Nullable
    private String getAccessTokenFromCookies(@Nullable Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AUTHORIZATION)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
