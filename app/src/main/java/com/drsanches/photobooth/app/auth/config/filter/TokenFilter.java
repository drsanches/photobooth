package com.drsanches.photobooth.app.auth.config.filter;

import com.drsanches.photobooth.app.auth.exception.AuthException;
import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.utils.TokenExtractor;
import com.drsanches.photobooth.app.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class TokenFilter extends GenericFilterBean {

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
        var uri = httpRequest.getRequestURI();
        try {
            if (!excludeUri.test(uri)) {
                //TODO: Refactor
                var token = TokenExtractor.getAccessTokenFromRequest(httpRequest)
                        .orElseThrow(WrongTokenException::new);
                var userInfo = tokenService.validate(token);
                request.setAttribute("userId", userInfo.getUserId());
            }
        } catch (AuthException e) {
            log.info("Wrong token for uri. Uri: {}", uri, e);
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().println(e.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }
}
