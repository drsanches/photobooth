package com.drsanches.photobooth.app.notifier.config.filter;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.utils.TokenExtractor;
import com.drsanches.photobooth.app.notifier.config.NotifierUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class NotifierFilter extends GenericFilterBean { //TODO: Rename

    private final NotifierUserInfo notifierUserInfo;
    private final AuthIntegrationService authIntegrationService;

    public NotifierFilter(
            NotifierUserInfo notifierUserInfo,
            AuthIntegrationService authIntegrationService
    ) {
        this.notifierUserInfo = notifierUserInfo;
        this.authIntegrationService = authIntegrationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var token = TokenExtractor.getAccessTokenFromRequest(httpRequest)
                .orElseThrow(WrongTokenException::new);
        authIntegrationService.getAuthInfo(token).ifPresentOrElse(
                authInfo -> notifierUserInfo.init(authInfo.userId()),
                () -> {
                    throw new WrongTokenException();
                }
        );
        chain.doFilter(request, response);
    }
}
