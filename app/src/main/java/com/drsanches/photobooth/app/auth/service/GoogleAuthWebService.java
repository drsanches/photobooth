package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.exception.EmailAlreadyInUseException;
import com.drsanches.photobooth.app.auth.exception.ForbiddenException;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import com.drsanches.photobooth.app.auth.utils.ConfirmationValidator;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.common.integration.app.AppIntegrationService;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@Validated
public class GoogleAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private AppIntegrationService appIntegrationService;
    @Autowired
    private GoogleUserInfoService googleUserInfoService;
    @Autowired
    private ConfirmationDomainService confirmationDomainService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthInfo authInfo;
    @Autowired
    private AuthExistenceValidator authExistenceValidator;
    @Autowired
    private ConfirmationValidator confirmationValidator;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TokenMapper tokenMapper;

    private final static Supplier<Instant> EXPIRES = () -> Instant.now().plus(5, ChronoUnit.MINUTES);

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
                appIntegrationService.safetyInitializeProfile(
                        userAuth.getId(),
                        userAuth.getUsername(),
                        googleInfo.getName(),
                        googleUserInfoService.safetyGetPicture(googleInfo.getPicture()) //TODO: Validate picture
                );
                log.info("New user created. Id: {}", userAuth.getId());
                confirmationCode = confirmationDomainService.create(
                        Operation.GOOGLE_USERNAME_CHANGE,
                        EXPIRES.get(),
                        userAuth.getId(),
                        null,
                        null,
                        null
                ).getCode();
                log.info("Google username changing process started. UserId: {}", userAuth.getId());
                notificationService.notify(Action.REGISTRATION_COMPLETED, NotificationParams.builder()
                        .email(userAuth.getEmail())
                        .build());
            }
        }
        var token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return new GoogleGetTokenDto(tokenMapper.convert(token), confirmationCode);
    }

    public void setUsername(@Valid GoogleSetUsernameDto googleSetUsernameDto) {
        var confirmation = confirmationDomainService.findByCode(googleSetUsernameDto.getCode())
                .orElseThrow(WrongConfirmCodeException::new);
        confirmationValidator.validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        authExistenceValidator.validateUsername(googleSetUsernameDto.getNewUsername());
        var userId = authInfo.getUserId();
        var oldUsername = userAuthDomainService.findEnabledById(userId)
                .orElseThrow()
                .getUsername();

        //TODO: Transaction
        appIntegrationService.updateUsername(userId, googleSetUsernameDto.getNewUsername()); //TODO: Is it needed?
        userAuthDomainService.updateUsername(userId, googleSetUsernameDto.getNewUsername());

        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed google default username. UserId: {}, oldUsername: {}, newUsername: {}",
                userId, oldUsername, googleSetUsernameDto.getNewUsername());
    }

    public void link(@Valid GoogleTokenDto googleTokenDto) {
        var userId = authInfo.getUserId();
        var email = googleUserInfoService.getGoogleInfo(googleTokenDto.getIdToken()).getEmail();
        userAuthDomainService.findEnabledByEmail(email).ifPresent(it -> {throw new EmailAlreadyInUseException();});
        link(userId, email);
    }

    private void link(String userId, String email) {
        userAuthDomainService.findEnabledByGoogleAuth(email).ifPresent(it -> {throw new EmailAlreadyInUseException();});
        userAuthDomainService.updateGoogleAuth(userId, email);
        log.info("Google account linked. UserId: {}", userId);
        var user = userAuthDomainService.findEnabledById(userId).orElseThrow();
        notificationService.notify(Action.ACCOUNT_LINKED, NotificationParams.builder()
                .email(user.getEmail())
                .account(email)
                .build());
    }

    public void unlink() {
        var userId = authInfo.getUserId();
        var user = userAuthDomainService.findEnabledById(userId).orElseThrow();
        if (user.getPassword() == null) {
            throw new ForbiddenException();
        }
        userAuthDomainService.updateGoogleAuth(userId, null);
        log.info("Google account unlinked. UserId: {}", userId);
        notificationService.notify(Action.ACCOUNT_UNLINKED, NotificationParams.builder()
                .email(user.getEmail())
                .account("Google")
                .build());
    }
}
