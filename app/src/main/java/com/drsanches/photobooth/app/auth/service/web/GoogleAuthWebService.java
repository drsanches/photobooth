package com.drsanches.photobooth.app.auth.service.web;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleGetTokenDTO;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleSetUsernameDTO;
import com.drsanches.photobooth.app.auth.service.domain.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.service.utils.ConfirmationCodeValidator;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.common.token.data.TokenMapper;
import com.drsanches.photobooth.app.auth.data.common.dto.request.GoogleTokenDTO;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.service.domain.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.service.integration.GoogleUserInfoService;
import com.drsanches.photobooth.app.auth.service.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.common.exception.auth.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationService;
import com.drsanches.photobooth.app.common.token.data.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Service
@Validated
public class GoogleAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private UserIntegrationService userIntegrationService;

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

    public GoogleGetTokenDTO getToken(@Valid GoogleTokenDTO googleTokenDTO) {
        String email = googleUserInfoService.getGoogleInfo(googleTokenDTO.getIdToken()).getEmail();
        UserAuth userAuth;
        String confirmationCode = null;
        try {
            userAuth = userAuthDomainService.getEnabledByGoogleAuth(email);
        } catch (NoGoogleUserException e) {
            userAuth = userIntegrationService.createUserByGoogle(email);
            log.info("New user created. Id: {}", userAuth.getId());
            confirmationCode = confirmationDomainService.create(null, userAuth.getId(), userAuth.getEmail(), Operation.GOOGLE_USERNAME_CHANGE).getCode();
            log.info("Google username changing process started. UserId: {}", userAuth.getId());
            emailNotifier.sendSuccessNotification(userAuth.getEmail(), Operation.REGISTRATION);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return new GoogleGetTokenDTO(tokenMapper.convert(token), confirmationCode);
    }

    public void setUsername(@Valid GoogleSetUsernameDTO googleSetUsernameDTO) {
        Confirmation confirmation = confirmationDomainService.get(googleSetUsernameDTO.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        String oldUsername = current.getUsername();
        current.setUsername(googleSetUsernameDTO.getNewUsername());
        userIntegrationService.updateUser(current);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed google default username. UserId: {}, oldUsername: {}, newUsername: {}", current.getId(), oldUsername, current.getUsername());
    }
}
