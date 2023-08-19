package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.utils.ConfirmationCodeValidator;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.auth.exception.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class GoogleAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;

    @Autowired
    private GoogleUserInfoService googleUserInfoService;

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private ConfirmationCodeValidator confirmationCodeValidator;

    @Autowired
    private EmailNotifier emailNotifier;

    @Autowired
    private TokenMapper tokenMapper;

    public GoogleGetTokenDto getToken(@Valid GoogleTokenDto googleTokenDto) {
        String email = googleUserInfoService.getGoogleInfo(googleTokenDto.getIdToken()).getEmail();
        UserAuth userAuth;
        String confirmationCode = null;
        try {
            userAuth = userAuthDomainService.getEnabledByGoogleAuth(email);
        } catch (NoGoogleUserException e) {
            userAuth = userIntegrationDomainService.createUserByGoogle(email);
            log.info("New user created. Id: {}", userAuth.getId());
            confirmationCode = confirmationDomainService.create(null, userAuth.getId(), userAuth.getEmail(), Operation.GOOGLE_USERNAME_CHANGE).getCode();
            log.info("Google username changing process started. UserId: {}", userAuth.getId());
            emailNotifier.sendSuccessNotification(userAuth.getEmail(), Operation.REGISTRATION);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return new GoogleGetTokenDto(tokenMapper.convert(token), confirmationCode);
    }

    public void setUsername(@Valid GoogleSetUsernameDto googleSetUsernameDto) {
        Confirmation confirmation = confirmationDomainService.get(googleSetUsernameDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        String userId = tokenSupplier.get().getUserId();
        String oldUsername = userAuthDomainService.getEnabledById(userId).getUsername();
        userIntegrationDomainService.updateUsername(userId, googleSetUsernameDto.getNewUsername());
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed google default username. UserId: {}, oldUsername: {}, newUsername: {}",
                userId, oldUsername, googleSetUsernameDto.getNewUsername());
    }
}
