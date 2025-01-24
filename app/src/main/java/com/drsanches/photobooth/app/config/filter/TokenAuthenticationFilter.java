package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.utils.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.function.Predicate;

@AllArgsConstructor
public class TokenAuthenticationFilter extends GenericFilterBean {

    private AuthIntegrationService authIntegrationService;
    private AuthInfo authInfo;
    private Predicate<String> cookiesAuthUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var token = cookiesAuthUri.test(((HttpServletRequest) request).getRequestURI()) ?
                TokenExtractor.getAccessTokenFromCookies(httpRequest) :
                TokenExtractor.getAccessTokenFromHeaders(httpRequest);

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var authInfoDto = authIntegrationService.getAuthInfo(token.get());
        if (authInfoDto.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        authInfo.init(
                authInfoDto.get().userId(),
                authInfoDto.get().username(),
                authInfoDto.get().tokenId()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new TestingAuthenticationToken( //TODO
                authInfoDto.get().username(),
                authInfoDto.get().tokenId(),
                authInfoDto.get().role()
        );
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}
