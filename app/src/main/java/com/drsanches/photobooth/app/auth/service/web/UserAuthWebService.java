package com.drsanches.photobooth.app.auth.service.web;

import com.drsanches.photobooth.app.auth.data.common.confirm.ChangePasswordConfirmData;
import com.drsanches.photobooth.app.auth.data.common.confirm.ChangeUsernameConfirmData;
import com.drsanches.photobooth.app.auth.data.common.confirm.RegistrationConfirmData;
import com.drsanches.photobooth.app.auth.data.common.dto.request.RegistrationDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.UserAuthInfoDTO;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.service.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.exception.auth.WrongConfirmCodeException;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.TokenMapper;
import com.drsanches.photobooth.app.auth.data.common.confirm.ChangeEmailConfirmData;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangeEmailDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangePasswordDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangeUsernameDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ConfirmationCodeDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.LoginDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.userauth.mapper.UserAuthInfoMapper;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.service.domain.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.service.domain.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.service.utils.StringSerializer;
import com.drsanches.photobooth.app.auth.service.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.common.exception.application.NoUsernameException;
import com.drsanches.photobooth.app.common.exception.auth.WrongPasswordException;
import com.drsanches.photobooth.app.common.exception.auth.WrongUsernamePasswordException;
import com.drsanches.photobooth.app.common.service.UserIntegrationService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.common.token.data.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.GregorianCalendar;
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
    private UserIntegrationService userIntegrationService;

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
    private UserAuthInfoMapper userAuthInfoMapper;

    @Autowired
    private EmailNotifier emailNotifier;

    @Value("${application.2FA-enabled}")
    private boolean with2FA;

    public TokenDTO registration(@Valid RegistrationDTO registrationDTO) {
        String salt = UUID.randomUUID().toString();
        RegistrationConfirmData registrationConfirmData = RegistrationConfirmData.builder()
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .encryptedPassword(credentialsHelper.encodePassword(registrationDTO.getPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(registrationConfirmData);
        Confirmation confirmation = confirmationDomainService.create(data, null, registrationConfirmData.getEmail(), Operation.REGISTRATION);
        if (with2FA) {
            emailNotifier.sendCode(confirmation.getCode(), confirmation.getEmail(), confirmation.getOperation());
        }
        log.info("User registration process started: {}", registrationConfirmData);
        return with2FA ? null : registrationConfirm(new ConfirmationCodeDTO(confirmation.getCode()));
    }

    public TokenDTO registrationConfirm(@Valid ConfirmationCodeDTO confirmationCodeDTO) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDTO.getCode());
        validate(confirmation, Operation.REGISTRATION);
        RegistrationConfirmData registrationConfirmData = stringSerializer.deserialize(confirmation.getData(), RegistrationConfirmData.class);
        UserAuth userAuth = userIntegrationService.createUser(
                registrationConfirmData.getUsername(),
                registrationConfirmData.getEmail(),
                registrationConfirmData.getEncryptedPassword(),
                registrationConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        log.info("New user created. UserId: {}", userAuth.getId());
        emailNotifier.sendSuccessNotification(confirmation.getEmail(), confirmation.getOperation());
        return tokenMapper.convert(token);
    }

    public TokenDTO login(@Valid LoginDTO loginDTO) {
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledByUsername(loginDTO.getUsername().toLowerCase());
            credentialsHelper.checkPassword(loginDTO.getPassword(), userAuth.getPassword(), userAuth.getSalt());
        } catch (NoUsernameException | WrongPasswordException e) {
            throw new WrongUsernamePasswordException(e);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }

    public UserAuthInfoDTO info() {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        return userAuthInfoMapper.convert(current);
    }

    public void changeUsername(@Valid ChangeUsernameDTO changeUsernameDTO) {
        ChangeUsernameConfirmData changeUsernameConfirmData = ChangeUsernameConfirmData.builder()
                .username(changeUsernameDTO.getNewUsername())
                .build();
        String data = stringSerializer.serialize(changeUsernameConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(data, userId, current.getEmail(), Operation.USERNAME_CHANGE);
        if (with2FA) {
            emailNotifier.sendCode(confirmation.getCode(), confirmation.getEmail(), confirmation.getOperation());
        }
        log.info("Username changing process started: {}", changeUsernameConfirmData);
        if (!with2FA) {
            changeUsernameConfirm(new ConfirmationCodeDTO(confirmation.getCode()));
        }
    }

    public void changeUsernameConfirm(@Valid ConfirmationCodeDTO confirmationCodeDTO) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDTO.getCode());
        validate(confirmation, Operation.USERNAME_CHANGE);
        ChangeUsernameConfirmData changeUsernameConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangeUsernameConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        String oldUsername = current.getUsername();
        current.setUsername(changeUsernameConfirmData.getUsername());
        userIntegrationService.updateUser(current);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed username. UserId: {}, oldUsername: {}, newUsername: {}", current.getId(), oldUsername, current.getUsername());
        emailNotifier.sendSuccessNotification(confirmation.getEmail(), confirmation.getOperation());
    }

    public void changePassword(@Valid ChangePasswordDTO changePasswordDTO) {
        String salt = UUID.randomUUID().toString();
        ChangePasswordConfirmData changePasswordConfirmData = ChangePasswordConfirmData.builder()
                .encryptedPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(changePasswordConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(data, userId, current.getEmail(), Operation.PASSWORD_CHANGE);
        if (with2FA) {
            emailNotifier.sendCode(confirmation.getCode(), confirmation.getEmail(), confirmation.getOperation());
        }
        log.info("Password changing process started: {}", changePasswordConfirmData);
        if (!with2FA) {
            changePasswordConfirm(new ConfirmationCodeDTO(confirmation.getCode()));
        }
    }

    public void changePasswordConfirm(@Valid ConfirmationCodeDTO confirmationCodeDTO) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDTO.getCode());
        validate(confirmation, Operation.PASSWORD_CHANGE);
        ChangePasswordConfirmData changePasswordConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangePasswordConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        current.setSalt(changePasswordConfirmData.getSalt());
        current.setPassword(changePasswordConfirmData.getEncryptedPassword());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User changed password. UserId: {}", current.getId());
        emailNotifier.sendSuccessNotification(confirmation.getEmail(), confirmation.getOperation());
    }

    public void changeEmail(@Valid ChangeEmailDTO changeEmailDTO) {
        ChangeEmailConfirmData changeEmailConfirmData = ChangeEmailConfirmData.builder()
                .email(changeEmailDTO.getNewEmail())
                .build();
        String data = stringSerializer.serialize(changeEmailConfirmData);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(data, userId, current.getEmail(), Operation.EMAIL_CHANGE);
        if (with2FA) {
            emailNotifier.sendCode(confirmation.getCode(), confirmation.getEmail(), confirmation.getOperation());
        }
        log.info("Email changing process started: {}", changeEmailConfirmData);
        if (!with2FA) {
            changeEmailConfirm(new ConfirmationCodeDTO(confirmation.getCode()));
        }
    }

    public void changeEmailConfirm(@Valid ConfirmationCodeDTO confirmationCodeDTO) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDTO.getCode());
        validate(confirmation, Operation.EMAIL_CHANGE);
        ChangeEmailConfirmData changeEmailConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangeEmailConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        current.setEmail(changeEmailConfirmData.getEmail());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(confirmation.getId());
        log.info("User changed email. UserId: {}", current.getId());
        emailNotifier.sendSuccessNotification(confirmation.getEmail(), confirmation.getOperation());
    }

    public TokenDTO refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void logout() {
        tokenService.removeCurrentToken();
    }

    public void disableUser() {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        Confirmation confirmation = confirmationDomainService.create(null, userId, current.getEmail(), Operation.DISABLE);
        if (with2FA) {
            emailNotifier.sendCode(confirmation.getCode(), confirmation.getEmail(), confirmation.getOperation());
        }
        log.info("User disabling process started. UserId: {}", userId);
        if (!with2FA) {
            disableUserConfirm(new ConfirmationCodeDTO(confirmation.getCode()));
        }
    }

    public void disableUserConfirm(@Valid ConfirmationCodeDTO confirmationCodeDTO) {
        Confirmation confirmation = confirmationDomainService.get(confirmationCodeDTO.getCode());
        validate(confirmation, Operation.DISABLE);
        String userId = tokenSupplier.get().getUserId();
        userIntegrationService.disableUser(userId);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User disabled. UserId: {}", userId);
        emailNotifier.sendSuccessNotification(confirmation.getEmail(), confirmation.getOperation());
    }

    private void validate(Confirmation confirmation, Operation operation) {
        if (confirmation.getUserId() != null && !confirmation.getUserId().equals(tokenSupplier.get().getUserId())
                || operation != confirmation.getOperation()) {
            throw new WrongConfirmCodeException();
        }
        if (confirmation.getExpiresAt().before(new GregorianCalendar())) {
            confirmationDomainService.delete(confirmation.getId());
            log.info("Expired Confirmation deleted: {}", confirmation);
            throw new WrongConfirmCodeException("Confirmation code expired");
        }
    }
}
