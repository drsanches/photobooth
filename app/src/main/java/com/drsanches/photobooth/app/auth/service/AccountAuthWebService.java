package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.dto.AuthResponse;
import com.drsanches.photobooth.app.auth.dto.confirm.ChangePasswordConfirmData;
import com.drsanches.photobooth.app.auth.dto.confirm.RegistrationConfirmData;
import com.drsanches.photobooth.app.auth.dto.userauth.request.CreateAccountDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import com.drsanches.photobooth.app.auth.utils.ConfirmationValidator;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.exception.ServerError;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.common.integration.app.AppIntegrationService;
import com.drsanches.photobooth.app.common.integration.notifier.NotifierIntegrationService;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdatePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.mapper.UserAuthInfoMapper;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.StringSerializer;
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
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@Validated
public class AccountAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private ConfirmationDomainService confirmationDomainService;
    @Autowired
    private AppIntegrationService appIntegrationService;
    @Autowired
    private NotifierIntegrationService notifierIntegrationService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthInfo authInfo;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private StringSerializer stringSerializer;
    @Autowired
    private AuthExistenceValidator authExistenceValidator;
    @Autowired
    private ConfirmationValidator confirmationValidator;
    @Autowired
    private UserAuthInfoMapper userAuthInfoMapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    private final static Supplier<Instant> EXPIRES = () -> Instant.now().plus(5, ChronoUnit.MINUTES);

    public AuthResponse<TokenDto> createAccount(@Valid CreateAccountDto createAccountDto) {
        var salt = UUID.randomUUID().toString();
        authExistenceValidator
                .validateUsername(createAccountDto.getUsername())
                .validateEmail(createAccountDto.getEmail());
        var confirmation = confirmationDomainService.create(
                Operation.REGISTRATION,
                EXPIRES.get(),
                null,
                createAccountDto.getUsername(),
                createAccountDto.getEmail(),
                stringSerializer.serialize(RegistrationConfirmData.builder()
                        .encryptedPassword(credentialsHelper.encodePassword(createAccountDto.getPassword(), salt))
                        .salt(salt)
                        .build())
        );
        if (twoFactorAuthenticationManager.isEnabled(Operation.REGISTRATION)) {
            notificationService.notify(Action.REGISTRATION_STARTED, NotificationParams.builder()
                    .email(createAccountDto.getEmail())
                    .code(confirmation.getCode())
                    .build());
            log.info("User registration process started: {}", confirmation);
            return new AuthResponse<>(true);
        } else {
            var tokenDto = (TokenDto) confirm(confirmation.getCode());
            return new AuthResponse<>(tokenDto, false);
        }
    }

    public UserAuthInfoDto getAccount() {
        var userId = authInfo.getUserId();
        var current = userAuthDomainService.findEnabledById(userId).orElseThrow();
        return userAuthInfoMapper.convert(current);
    }

    public AuthResponse<Void> updateUsername(@Valid UpdateUsernameDto updateUsernameDto) {
        var userId = authInfo.getUserId();
        authExistenceValidator.validateUsername(updateUsernameDto.getNewUsername());
        var confirmation = confirmationDomainService.create(
                Operation.USERNAME_CHANGE,
                EXPIRES.get(),
                userId,
                updateUsernameDto.getNewUsername(),
                null,
                null
        );
        if (twoFactorAuthenticationManager.isEnabled(Operation.USERNAME_CHANGE)) {
            notificationService.notify(Action.USERNAME_CHANGE_STARTED, NotificationParams.builder()
                    .userId(confirmation.getUserId())
                    .code(confirmation.getCode())
                    .build());
            log.info("Username changing process started: {}", confirmation);
            return new AuthResponse<>(true);
        } else {
            confirm(confirmation.getCode());
            return new AuthResponse<>(false);
        }
    }

    public AuthResponse<Void> updatePassword(@Valid UpdatePasswordDto updatePasswordDto) {
        var salt = UUID.randomUUID().toString();
        var userId = authInfo.getUserId();
        var confirmation = confirmationDomainService.create(
                Operation.PASSWORD_CHANGE,
                EXPIRES.get(),
                userId,
                null,
                null,
                stringSerializer.serialize(ChangePasswordConfirmData.builder()
                        .encryptedPassword(credentialsHelper.encodePassword(updatePasswordDto.getNewPassword(), salt))
                        .salt(salt)
                        .build())
        );
        if (twoFactorAuthenticationManager.isEnabled(Operation.PASSWORD_CHANGE)) {
            notificationService.notify(Action.PASSWORD_CHANGE_STARTED, NotificationParams.builder()
                    .userId(confirmation.getUserId())
                    .code(confirmation.getCode())
                    .build());
            log.info("Password changing process started: {}", confirmation);
            return new AuthResponse<>(true);
        } else {
            confirm(confirmation.getCode());
            return new AuthResponse<>(false);
        }
    }

    public AuthResponse<Void> updateEmail(@Valid UpdateEmailDto updateEmailDto) {
        var userId = authInfo.getUserId();
        authExistenceValidator.validateEmail(updateEmailDto.getNewEmail());
        var confirmation = confirmationDomainService.create(
                Operation.EMAIL_CHANGE,
                EXPIRES.get(),
                userId,
                null,
                updateEmailDto.getNewEmail(),
                null
        );
        if (twoFactorAuthenticationManager.isEnabled(Operation.EMAIL_CHANGE)) {
            notificationService.notify(Action.EMAIL_CHANGE_STARTED, NotificationParams.builder()
                    .userId(confirmation.getUserId())
                    .code(confirmation.getCode())
                    .build());
            log.info("Email changing process started: {}", confirmation);
            return new AuthResponse<>(true);
        } else {
            confirm(confirmation.getCode());
            return new AuthResponse<>(false);
        }
    }

    public AuthResponse<Void> disableUser() {
        var userId = authInfo.getUserId();
        var confirmation = confirmationDomainService.create(
                Operation.DISABLE,
                EXPIRES.get(),
                userId,
                null,
                null,
                null
        );
        if (twoFactorAuthenticationManager.isEnabled(Operation.DISABLE)) {
            notificationService.notify(Action.DISABLE_STARTED, NotificationParams.builder()
                    .userId(confirmation.getUserId())
                    .code(confirmation.getCode())
                    .build());
            log.info("User disabling process started. UserId: {}", userId);
            return new AuthResponse<>(true);
        } else {
            confirm(confirmation.getCode());
            return new AuthResponse<>(false);
        }
    }

    public Object confirm(@Valid String confirmationCode) {
        var confirmation = confirmationDomainService.findByCode(confirmationCode)
                .orElseThrow(WrongConfirmCodeException::new);
        confirmationValidator.validate(confirmation);
        return switch (confirmation.getOperation()) {
            case REGISTRATION -> registrationConfirm(confirmation);
            case USERNAME_CHANGE -> changeUsernameConfirm(confirmation);
            case PASSWORD_CHANGE -> changePasswordConfirm(confirmation);
            case EMAIL_CHANGE -> changeEmailConfirm(confirmation);
            case DISABLE -> disableUserConfirm(confirmation);
            default -> throw new ServerError("Unsupported operation");
        };
    }

    public TokenDto registrationConfirm(Confirmation confirmation) {
        var registrationConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                RegistrationConfirmData.class
        );
        var userAuth = userAuthDomainService.createUser(
                confirmation.getNewUsername(),
                confirmation.getNewEmail(),
                registrationConfirmData.getEncryptedPassword(),
                registrationConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        var token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        log.info("New user created. UserId: {}", userAuth.getId());
        notificationService.notify(Action.REGISTRATION_COMPLETED, NotificationParams.builder()
                .userId(userAuth.getId())
                .build());
        return tokenMapper.convert(token);
    }

    public Object changeUsernameConfirm(Confirmation confirmation) {
        var userId = confirmation.getUserId();
        var oldUsername = userAuthDomainService.findEnabledById(userId)
                .orElseThrow()
                .getUsername();

        //TODO: Transaction
        appIntegrationService.updateUsername(userId, confirmation.getNewUsername()); //TODO: Is it needed?
        userAuthDomainService.updateUsername(userId, confirmation.getNewUsername());

        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed username. UserId: {}, oldUsername: {}, newUsername: {}",
                userId, oldUsername, confirmation.getNewUsername());
        notificationService.notify(Action.USERNAME_CHANGE_COMPLETED, NotificationParams.builder()
                .userId(userId)
                .build());
        return null;
    }

    public Object changePasswordConfirm(Confirmation confirmation) {
        var changePasswordConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                ChangePasswordConfirmData.class
        );
        var userId = confirmation.getUserId();
        userAuthDomainService.updatePassword(
                userId,
                changePasswordConfirmData.getEncryptedPassword(),
                changePasswordConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed password. UserId: {}", userId);
        notificationService.notify(Action.PASSWORD_CHANGE_COMPLETED, NotificationParams.builder()
                .userId(userId)
                .build());
        return null;
    }

    public Object changeEmailConfirm(Confirmation confirmation) {
        var userId = confirmation.getUserId();

        //TODO: Transaction
        notifierIntegrationService.updateEmail(userId, confirmation.getNewEmail());
        userAuthDomainService.updateEmail(userId, confirmation.getNewEmail());

        confirmationDomainService.delete(confirmation.getId());
        log.info("User changed email. UserId: {}", userId);
        notificationService.notify(Action.EMAIL_CHANGE_COMPLETED, NotificationParams.builder()
                .userId(userId)
                .build());
        return null;
    }

    public Object disableUserConfirm(Confirmation confirmation) {
        var userId = confirmation.getUserId();
        var email = userAuthDomainService.findEnabledById(userId)
                .orElseThrow()
                .getEmail();

        //TODO: Transaction
        appIntegrationService.disable(userId);
        userAuthDomainService.disableUser(userId);
        notifierIntegrationService.removeEmail(userId);

        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User disabled. UserId: {}", userId);
        notificationService.notify(Action.DISABLE_COMPLETED, NotificationParams.builder()
                .email(email)
                .build());
        return null;
    }
}
