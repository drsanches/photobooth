package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.integration.auth.AuthInfoDto;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthIntegrationServiceImpl implements AuthIntegrationService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private TokenService tokenService;

    @Override
    public Optional<AuthInfoDto> getAuthInfo(String token) {
        var userInfo = tokenService.validate(token);
        var userAuth = userAuthDomainService.getEnabledById(userInfo.getUserId());
        return Optional.of(new AuthInfoDto(userAuth.getId(), userAuth.getUsername()));
    }

    @Override
    public String getEmail(String userId) {
        return userAuthDomainService.getEnabledById(userId).getEmail();
    }
}
