package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
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
        try {
            return Optional.of(tokenService.validate(token));
        } catch (WrongTokenAuthException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getEmail(String userId) {
        return userAuthDomainService.getEnabledById(userId).getEmail();
    }
}
