package com.drsanches.photobooth.app.app.config.filter;

import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.service.AuthInfoDto;
import com.drsanches.photobooth.app.common.service.AuthIntegrationService;
import com.drsanches.photobooth.app.common.service.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class UserProfileFilter extends GenericFilterBean { //TODO: Rename

    private final UserProfileDomainService userProfileDomainService;
    private final AuthIntegrationService authIntegrationService;
    private final Predicate<String> excludeUri;

    public UserProfileFilter(
            UserProfileDomainService userProfileDomainService,
            AuthIntegrationService authIntegrationService,
            Predicate<String> excludeUri
    ) {
        this.userProfileDomainService = userProfileDomainService;
        this.authIntegrationService = authIntegrationService;
        this.excludeUri = excludeUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var uri = httpRequest.getRequestURI();
        if (!excludeUri.test(uri)) {
            var token = TokenExtractor.getAccessTokenFromRequest(httpRequest)
                    .orElseThrow(WrongTokenException::new);
            authIntegrationService.getAuthInfo(token).ifPresentOrElse(
                    authInfo -> userProfileDomainService.findById(authInfo.userId()).ifPresentOrElse(
                            userProfile -> updateUsernameIfNecessary(userProfile, authInfo),
                            () -> userProfileDomainService.create(authInfo.userId(), authInfo.username())
                    ),
                    () -> {
                        throw new WrongTokenException();
                    }
            );
        }
        chain.doFilter(request, response);
    }

    private void updateUsernameIfNecessary(UserProfile userProfile, AuthInfoDto authInfo) {
        if (!userProfile.getUsername().equals(authInfo.username())) {
            userProfileDomainService.updateUsername(
                    authInfo.userId(),
                    authInfo.username()
            );
        }
    }
}
