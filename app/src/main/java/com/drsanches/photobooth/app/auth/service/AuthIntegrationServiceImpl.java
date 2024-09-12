package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.integration.auth.AuthInfoDto;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.integration.auth.UserCreationInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthIntegrationServiceImpl implements AuthIntegrationService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private AuthExistenceValidator authExistenceValidator;

    @Override
    public UserCreationInfoDto createAccount(String username, String email, String password) {
        var salt = UUID.randomUUID().toString();
        authExistenceValidator
                .validateUsername(username)
                .validateEmail(email);
        var user = userAuthDomainService.createUser(
                username,
                email,
                credentialsHelper.encodePassword(password, salt),
                salt
        );
        return new UserCreationInfoDto(user.getId(), user.getUsername(), user.getEmail());
    }

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
        return userAuthDomainService.findEnabledById(userId)
                .orElseThrow()
                .getEmail();
    }
}
