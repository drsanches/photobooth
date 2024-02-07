package com.drsanches.photobooth.app.app.config.filter;

import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.service.AuthIntegrationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class UserProfileFilter extends GenericFilterBean { //TODO: Rename

    private final UserProfileDomainService userProfileDomainService;
    private final AuthIntegrationService authIntegrationService;

    public UserProfileFilter(
            UserProfileDomainService userProfileDomainService,
            AuthIntegrationService authIntegrationService
    ) {
        this.userProfileDomainService = userProfileDomainService;
        this.authIntegrationService = authIntegrationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var userId = request.getAttribute("userId");
        if (userId != null) {
            var userProfile = userProfileDomainService.findById(userId.toString());
            if (userProfile.isEmpty()) {
                userProfileDomainService.create(
                        userId.toString(),
                        authIntegrationService.getUsername(userId.toString())
                );
            }
        }
        chain.doFilter(request, response);
    }
}
