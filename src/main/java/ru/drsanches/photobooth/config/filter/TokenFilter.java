package ru.drsanches.photobooth.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.photobooth.exception.auth.AuthException;
import ru.drsanches.photobooth.common.token.TokenService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class TokenFilter extends GenericFilterBean {

    private final TokenService TOKEN_SERVICE;

    private final Predicate<String> EXCLUDE_URI;

    public TokenFilter(TokenService tokenService, Predicate<String> excludeUri) {
        this.TOKEN_SERVICE = tokenService;
        this.EXCLUDE_URI = excludeUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse  httpResponse = (HttpServletResponse) response;
        String token = getAccessTokenFromRequest(httpRequest);
        String uri = httpRequest.getRequestURI();
        try {
            TOKEN_SERVICE.validate(token);
        } catch (AuthException e) {
            if (!EXCLUDE_URI.test(uri)) {
                log.info("Wrong token for uri '{}' {}", uri, e.log());
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.getOutputStream().flush();
                httpResponse.getOutputStream().println(e.getMessage());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            token = getAccessTokenFromCookies(httpRequest.getCookies());
        }
        return token;
    }

    private String getAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
