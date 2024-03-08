package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.auth.exception.AuthException;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
@AllArgsConstructor
public class UserProfileSyncFilter extends GenericFilterBean { //TODO: Rename

    private final AuthInfo authInfo;
    private final UserProfileDomainService userProfileDomainService;
    private final Predicate<String> excludeUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var uri = httpRequest.getRequestURI();
        try {
            if (!excludeUri.test(uri)) {
                userProfileDomainService.findById(authInfo.getUserId()).ifPresentOrElse(
                        this::updateUsernameIfNecessary,
                        () -> userProfileDomainService.create(authInfo.getUserId(), authInfo.getUsername())
                );
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

    private void updateUsernameIfNecessary(UserProfile userProfile) {
        if (!userProfile.getUsername().equals(authInfo.getUsername())) {
            userProfileDomainService.updateUsername(
                    authInfo.getUserId(),
                    authInfo.getUsername()
            );
        }
    }
}