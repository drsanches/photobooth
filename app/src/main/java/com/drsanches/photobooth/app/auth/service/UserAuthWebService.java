package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.dto.confirm.ChangePasswordConfirmData;
import com.drsanches.photobooth.app.auth.dto.confirm.ChangeUsernameConfirmData;
import com.drsanches.photobooth.app.auth.dto.confirm.RegistrationConfirmData;
import com.drsanches.photobooth.app.auth.dto.userauth.request.RegistrationDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.utils.ConfirmationCodeValidator;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.dto.confirm.ChangeEmailConfirmData;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ConfirmationCodeDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.mapper.UserAuthInfoMapper;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.StringSerializer;
import com.drsanches.photobooth.app.notifier.Action;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import com.drsanches.photobooth.app.auth.exception.WrongPasswordException;
import com.drsanches.photobooth.app.auth.exception.WrongUsernamePasswordException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.notifier.Notifier;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Validated
public class UserAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private StringSerializer stringSerializer;

    @Autowired
    private ConfirmationCodeValidator confirmationCodeValidator;

    @Autowired
    private UserAuthInfoMapper userAuthInfoMapper;

    @Autowired
    private Notifier notifier;

    @Value("${application.email-notifications.2FA-enabled}")
    private boolean with2FA;

    public TokenDto registration(@Valid RegistrationDto registrationDto) {
        String salt = UUID.randomUUID().toString();
        RegistrationConfirmData registrationConfirmData = RegistrationConfirmData.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .encryptedPassword(credentialsHelper.encodePassword(registrationDto.getPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(registrationConfirmData);
        Confirmation confirmation = confirmationDomainService.create(
                data,
                null,
                registrationConfirmData.getEmail(),
                Operation.REGISTRATION
        );
        if (with2FA) {
            notifier.notify(
                    Action.REGISTRATION_STARTED,
                    Map.of("code", confirmation.getCode(), "email", confirmation.getEmail())
            );
        }
        log.info("User registration process started: {}", registrationConfirmData);
        return with2FA ? null : registrationConfirm(new ConfirmationCodeDto(confirmation.getCode()));
    }

    public TokenDto registrationConfirm(@Valid ConfirmationCodeDto confirmationCodeDto) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.REGISTRATION);
        RegistrationConfirmData registrationConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                RegistrationConfirmData.class
        );
        UserAuth userAuth = userIntegrationDomainService.createUser(
                registrationConfirmData.getUsername(),
                registrationConfirmData.getEmail(),
                registrationConfirmData.getEncryptedPassword(),
                registrationConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        log.info("New user created. UserId: {}", userAuth.getId());
        notifier.notify(Action.REGISTRATION_COMPLETED, Map.of("email", confirmation.getEmail()));
        return tokenMapper.convert(token);
    }

    public TokenDto login(@Valid LoginDto loginDto) {
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledByUsername(loginDto.getUsername().toLowerCase());
            credentialsHelper.checkPassword(loginDto.getPassword(), userAuth.getPassword(), userAuth.getSalt());
        } catch (NoUsernameException | WrongPasswordException e) {
            throw new WrongUsernamePasswordException(e);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }

    public UserAuthInfoDto info() {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        return userAuthInfoMapper.convert(current);
    }

    public void changeUsername(@Valid ChangeUsernameDto changeUsernameDto) {
        ChangeUsernameConfirmData changeUsernameConfirmData = ChangeUsernameConfirmData.builder()
                .username(changeUsernameDto.getNewUsername())
                .build();
        String data = stringSerializer.serialize(changeUsernameConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(
                data,
                userId,
                current.getEmail(),
                Operation.USERNAME_CHANGE
        );
        if (with2FA) {
            notifier.notify(
                    Action.USERNAME_CHANGE_STARTED,
                    Map.of("code", confirmation.getCode(), "email", confirmation.getEmail())
            );
        }
        log.info("Username changing process started: {}", changeUsernameConfirmData);
        if (!with2FA) {
            changeUsernameConfirm(new ConfirmationCodeDto(confirmation.getCode()));
        }
    }

    public void changeUsernameConfirm(@Valid ConfirmationCodeDto confirmationCodeDto) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.USERNAME_CHANGE);
        ChangeUsernameConfirmData changeUsernameConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                ChangeUsernameConfirmData.class
        );
        String userId = tokenSupplier.get().getUserId();
        String oldUsername = userAuthDomainService.getEnabledById(userId).getUsername();
        userIntegrationDomainService.updateUsername(userId, changeUsernameConfirmData.getUsername());
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed username. UserId: {}, oldUsername: {}, newUsername: {}",
                userId, oldUsername, changeUsernameConfirmData.getUsername());
        notifier.notify(Action.USERNAME_CHANGE_COMPLETED, Map.of("email", confirmation.getEmail()));
    }

    public void changePassword(@Valid ChangePasswordDto changePasswordDto) {
        String salt = UUID.randomUUID().toString();
        ChangePasswordConfirmData changePasswordConfirmData = ChangePasswordConfirmData.builder()
                .encryptedPassword(credentialsHelper.encodePassword(changePasswordDto.getNewPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(changePasswordConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(
                data,
                userId,
                current.getEmail(),
                Operation.PASSWORD_CHANGE
        );
        if (with2FA) {
            notifier.notify(
                    Action.PASSWORD_CHANGE_STARTED,
                    Map.of("code", confirmation.getCode(), "email", confirmation.getEmail())
            );
        }
        log.info("Password changing process started: {}", changePasswordConfirmData);
        if (!with2FA) {
            changePasswordConfirm(new ConfirmationCodeDto(confirmation.getCode()));
        }
    }

    public void changePasswordConfirm(@Valid ConfirmationCodeDto confirmationCodeDto) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.PASSWORD_CHANGE);
        ChangePasswordConfirmData changePasswordConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                ChangePasswordConfirmData.class
        );
        String userId = tokenSupplier.get().getUserId();
        userAuthDomainService.updatePassword(
                userId,
                changePasswordConfirmData.getEncryptedPassword(),
                changePasswordConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed password. UserId: {}", userId);
        notifier.notify(Action.PASSWORD_CHANGE_COMPLETED, Map.of("email", confirmation.getEmail()));
    }

    public void changeEmail(@Valid ChangeEmailDto changeEmailDto) {
        ChangeEmailConfirmData changeEmailConfirmData = ChangeEmailConfirmData.builder()
                .email(changeEmailDto.getNewEmail())
                .build();
        String data = stringSerializer.serialize(changeEmailConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(
                data,
                userId,
                current.getEmail(),
                Operation.EMAIL_CHANGE
        );
        if (with2FA) {
            notifier.notify(
                    Action.EMAIL_CHANGE_STARTED,
                    Map.of("code", confirmation.getCode(), "email", confirmation.getEmail())
            );
        }
        log.info("Email changing process started: {}", changeEmailConfirmData);
        if (!with2FA) {
            changeEmailConfirm(new ConfirmationCodeDto(confirmation.getCode()));
        }
    }

    public void changeEmailConfirm(@Valid ConfirmationCodeDto confirmationCodeDto) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.EMAIL_CHANGE);
        ChangeEmailConfirmData changeEmailConfirmData = stringSerializer.deserialize(
                confirmation.getData(),
                ChangeEmailConfirmData.class
        );
        String userId = tokenSupplier.get().getUserId();
        userAuthDomainService.updateEmail(userId, changeEmailConfirmData.getEmail());
        confirmationDomainService.delete(confirmation.getId());
        log.info("User changed email. UserId: {}", userId);
        notifier.notify(Action.EMAIL_CHANGE_COMPLETED, Map.of("email", confirmation.getEmail()));
    }

    public TokenDto refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void logout() {
        tokenService.removeCurrentToken();
    }

    public void disableUser() {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(
                null,
                userId,
                current.getEmail(),
                Operation.DISABLE
        );
        if (with2FA) {
            notifier.notify(
                    Action.DISABLE_STARTED,
                    Map.of("code", confirmation.getCode(), "email", confirmation.getEmail())
            );
        }
        log.info("User disabling process started. UserId: {}", userId);
        if (!with2FA) {
            disableUserConfirm(new ConfirmationCodeDto(confirmation.getCode()));
        }
    }

    public void disableUserConfirm(@Valid ConfirmationCodeDto confirmationCodeDto) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDto.getCode());
        confirmationCodeValidator.validate(confirmation, Operation.DISABLE);
        String userId = tokenSupplier.get().getUserId();
        userIntegrationDomainService.disableUser(userId);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User disabled. UserId: {}", userId);
        notifier.notify(Action.DISABLE_COMPLETED, Map.of("email", confirmation.getEmail()));
    }
}
