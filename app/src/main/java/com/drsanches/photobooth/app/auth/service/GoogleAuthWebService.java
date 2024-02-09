package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.utils.ConfirmationValidator;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.notifier.NotificationParams;
import com.drsanches.photobooth.app.common.service.AppIntegrationService;
import com.drsanches.photobooth.app.common.service.UserProfileIntegrationService;
import com.drsanches.photobooth.app.auth.config.AuthInfo;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.common.notifier.NotificationService;
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
    private AppIntegrationService appIntegrationService;

    @Autowired
    private UserProfileIntegrationService userProfileIntegrationService;

    @Autowired
    private GoogleUserInfoService googleUserInfoService;

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthInfo authInfo;

    @Autowired
    private ConfirmationValidator confirmationValidator;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TokenMapper tokenMapper;

    public GoogleGetTokenDto getToken(@Valid GoogleTokenDto googleTokenDto) {
        var googleInfo = googleUserInfoService.getGoogleInfo(googleTokenDto.getIdToken());
        var email = googleInfo.getEmail();
        String confirmationCode = null;
        UserAuth userAuth;
        var optionalUserAuth = userAuthDomainService.findEnabledByGoogleAuth(email);

        if (optionalUserAuth.isPresent()) {
            userAuth = optionalUserAuth.get();
        } else {
            optionalUserAuth = userAuthDomainService.findEnabledByEmail(email);

            if (optionalUserAuth.isPresent()) {
                userAuth = optionalUserAuth.get();
                link(userAuth.getId(), email);
            } else {
                userAuth = userAuthDomainService.createUserByGoogle(email);
                userProfileIntegrationService.safetyInitializeProfile(
                        userAuth.getId(),
                        googleInfo.getName(),
                        googleUserInfoService.safetyGetPicture(googleInfo.getPicture()) //TODO: Validate picture
                );
                log.info("New user created. Id: {}", userAuth.getId());
                confirmationCode = confirmationDomainService.create(
                        null,
                        userAuth.getId(),
                        Operation.GOOGLE_USERNAME_CHANGE
                ).getCode();
                log.info("Google username changing process started. UserId: {}", userAuth.getId());
                notificationService.notify(Action.REGISTRATION_COMPLETED, NotificationParams.builder()
                        .userId(userAuth.getId())
                        .build());
            }
        }
        var token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return new GoogleGetTokenDto(tokenMapper.convert(token), confirmationCode);
    }

    public void setUsername(@Valid GoogleSetUsernameDto googleSetUsernameDto) {
        var confirmation = confirmationDomainService.get(googleSetUsernameDto.getCode());
        confirmationValidator.validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        var userId = authInfo.getUserId();
        var oldUsername = userAuthDomainService.getEnabledById(userId).getUsername();

        //TODO: Transaction
        appIntegrationService.updateUsername(userId, googleSetUsernameDto.getNewUsername());
        userAuthDomainService.updateUsername(userId, googleSetUsernameDto.getNewUsername());

        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed google default username. UserId: {}, oldUsername: {}, newUsername: {}",
                userId, oldUsername, googleSetUsernameDto.getNewUsername());
    }

    public void link(@Valid GoogleTokenDto googleTokenDto) {
        var userId = authInfo.getUserId();
        var email = googleUserInfoService.getGoogleInfo(googleTokenDto.getIdToken()).getEmail();
        link(userId, email);
    }

    private void link(String userId, String email) {
        userAuthDomainService.setGoogleAuth(userId, email);
        log.info("Google account linked. UserId: {}", userId);
        notificationService.notify(Action.ACCOUNT_LINKED, NotificationParams.builder()
                .userId(userId)
                .email(email)
                .build());
    }

    public void unlink() {
        var userId = authInfo.getUserId();
        userAuthDomainService.setGoogleAuth(userId, null);
        log.info("Google account unlinked. UserId: {}", userId);
        notificationService.notify(Action.ACCOUNT_UNLINKED, NotificationParams.builder()
                .userId(userId)
                .account("Google")
                .build());
    }
}
