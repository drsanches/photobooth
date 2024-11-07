package com.drsanches.photobooth.app.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class TokenExtractor {

    private final static String AUTHORIZATION = "Authorization";

    public static Optional<String> getAccessTokenFromHeaders(HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader(AUTHORIZATION);
        if (token != null) {
            return Optional.of(token);
        }
        return Optional.empty();
    }

    public static Optional<String> getAccessTokenFromCookies(HttpServletRequest httpRequest) {
        var cookies = httpRequest.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AUTHORIZATION)) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }
}
