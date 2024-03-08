package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.utils.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
@AllArgsConstructor
public class TokenFilter extends GenericFilterBean {

    private final AuthIntegrationService authIntegrationService;
    private final Predicate<String> excludeUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var uri = httpRequest.getRequestURI();
        if (!excludeUri.test(uri)) {
            TokenExtractor.getAccessTokenFromRequest(httpRequest)
                    .flatMap(authIntegrationService::getAuthInfo)
                    .ifPresent(authInfo -> {
                        request.setAttribute("userId", authInfo.userId());
                        request.setAttribute("username", authInfo.username());
                        request.setAttribute("role", authInfo.role());
                    });
        }
        chain.doFilter(request, response);
    }
}
