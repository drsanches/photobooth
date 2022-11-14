package ru.drsanches.photobooth.auth.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.common.confirm.ChangeEmailConfirmData;
import ru.drsanches.photobooth.auth.data.common.confirm.ChangePasswordConfirmData;
import ru.drsanches.photobooth.auth.data.common.confirm.RegistrationConfirmData;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeEmailDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangePasswordDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeUsernameDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.LoginDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.RegistrationDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.TokenDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.data.common.confirm.ChangeUsernameConfirmData;
import ru.drsanches.photobooth.auth.data.confirmation.model.Confirmation;
import ru.drsanches.photobooth.auth.service.domain.ConfirmationDomainService;
import ru.drsanches.photobooth.auth.service.domain.UserAuthDomainService;
import ru.drsanches.photobooth.auth.service.utils.CredentialsHelper;
import ru.drsanches.photobooth.auth.data.userauth.mapper.UserAuthInfoMapper;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;
import ru.drsanches.photobooth.auth.service.utils.StringSerializer;
import ru.drsanches.photobooth.exception.application.NoUsernameException;
import ru.drsanches.photobooth.exception.auth.WrongPasswordException;
import ru.drsanches.photobooth.exception.auth.WrongUsernamePasswordException;
import ru.drsanches.photobooth.exception.application.ApplicationException;
import ru.drsanches.photobooth.common.integration.UserIntegrationService;
import ru.drsanches.photobooth.common.token.TokenService;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import ru.drsanches.photobooth.common.token.data.Token;
import ru.drsanches.photobooth.common.token.data.TokenMapper;

import javax.validation.Valid;
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
        Confirmation confirmation = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New user registration process has been started: {}", registrationConfirmData);
        return with2FA ? null : registrationConfirm(confirmation.getCode());
    }

    public TokenDTO registrationConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        RegistrationConfirmData registrationConfirmData = stringSerializer.deserialize(confirmation.getData(), RegistrationConfirmData.class);
        UserAuth userAuth = userIntegrationService.createUser(
                registrationConfirmData.getUsername(),
                registrationConfirmData.getEmail(),
                registrationConfirmData.getEncryptedPassword(),
                registrationConfirmData.getSalt()
        );
        confirmationDomainService.delete(confirmation.getId());
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        log.info("New user with id '{}' has been created", userAuth.getId());
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
        Confirmation confirmation = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New username changing process has been started: {}", changeUsernameConfirmData);
        if (!with2FA) {
            changeUsernameConfirm(confirmation.getCode());
        }
    }

    public void changeUsernameConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangeUsernameConfirmData changeUsernameConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangeUsernameConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        String oldUsername = current.getUsername();
        current.setUsername(changeUsernameConfirmData.getUsername());
        userIntegrationService.updateUser(current);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed username from '{}' to '{}'", current.getId(), oldUsername, current.getUsername());
    }

    public void changePassword(@Valid ChangePasswordDTO changePasswordDTO) {
        String salt = UUID.randomUUID().toString();
        ChangePasswordConfirmData changePasswordConfirmData = ChangePasswordConfirmData.builder()
                .encryptedPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(changePasswordConfirmData);
        Confirmation confirmation = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New password changing process has been started: {}", changePasswordConfirmData);
        if (!with2FA) {
            changePasswordConfirm(confirmation.getCode());
        }
    }

    public void changePasswordConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangePasswordConfirmData changePasswordConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangePasswordConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        current.setSalt(changePasswordConfirmData.getSalt());
        current.setPassword(changePasswordConfirmData.getEncryptedPassword());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(confirmation.getId());
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed password", current.getId());
    }

    public void changeEmail(@Valid ChangeEmailDTO changeEmailDTO) {
        ChangeEmailConfirmData changeEmailConfirmData = ChangeEmailConfirmData.builder()
                .email(changeEmailDTO.getNewEmail())
                .build();
        String data = stringSerializer.serialize(changeEmailConfirmData);
        Confirmation confirmation = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New email changing process has been started: {}", changeEmailConfirmData);
        if (!with2FA) {
            changeEmailConfirm(confirmation.getCode());
        }
    }

    public void changeEmailConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangeEmailConfirmData changeEmailConfirmData = stringSerializer.deserialize(confirmation.getData(), ChangeEmailConfirmData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        if (current.getEmail().equals(changeEmailConfirmData.getEmail())) { //TODO: Move to changeEmail
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailConfirmData.getEmail());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(confirmation.getId());
        log.info("User with id '{}' changed email", current.getId());
    }

    public TokenDTO refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void logout() {
        tokenService.removeCurrentToken();
    }

    public void disableUser() {
        String userId = tokenSupplier.get().getUserId();
        tokenService.removeAllTokens(userId);
        userIntegrationService.disableUser(userId);
        log.info("User with id '{}' has been disabled", userId);
    }
}
