package ru.drsanches.photobooth.auth.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.common.serializetion.ChangeEmailData;
import ru.drsanches.photobooth.auth.data.common.serializetion.ChangePasswordData;
import ru.drsanches.photobooth.auth.data.common.serializetion.RegistrationData;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeEmailDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangePasswordDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeUsernameDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.LoginDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.RegistrationDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.TokenDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.data.common.serializetion.ChangeUsernameData;
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
        RegistrationData registrationData = RegistrationData.builder()
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .encryptedPassword(credentialsHelper.encodePassword(registrationDTO.getPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(registrationData);
        String code = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New user registration process has been started: {}", registrationData);
        return with2FA ? null : registrationConfirm(code);
    }

    public TokenDTO registrationConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        RegistrationData registrationData = stringSerializer.deserialize(confirmation.getData(), RegistrationData.class);
        UserAuth userAuth = userIntegrationService.createUser(
                registrationData.getUsername(),
                registrationData.getEmail(),
                registrationData.getEncryptedPassword(),
                registrationData.getSalt()
        );
        confirmationDomainService.delete(code);
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
        ChangeUsernameData changeUsernameData = ChangeUsernameData.builder()
                .username(changeUsernameDTO.getNewUsername())
                .build();
        String data = stringSerializer.serialize(changeUsernameData);
        String code = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New username changing process has been started: {}", changeUsernameData);
        if (!with2FA) {
            changeUsernameConfirm(code);
        }
    }

    public void changeUsernameConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangeUsernameData changeUsernameData = stringSerializer.deserialize(confirmation.getData(), ChangeUsernameData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        String oldUsername = current.getUsername();
        current.setUsername(changeUsernameData.getUsername());
        userIntegrationService.updateUser(current);
        confirmationDomainService.delete(code);
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed username from '{}' to '{}'", current.getId(), oldUsername, current.getUsername());
    }

    public void changePassword(@Valid ChangePasswordDTO changePasswordDTO) {
        String salt = UUID.randomUUID().toString();
        ChangePasswordData changePasswordData = ChangePasswordData.builder()
                .encryptedPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword(), salt))
                .salt(salt)
                .build();
        String data = stringSerializer.serialize(changePasswordData);
        String code = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New password changing process has been started: {}", changePasswordData);
        if (!with2FA) {
            changePasswordConfirm(code);
        }
    }

    public void changePasswordConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangePasswordData changePasswordData = stringSerializer.deserialize(confirmation.getData(), ChangePasswordData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        current.setSalt(changePasswordData.getSalt());
        current.setPassword(changePasswordData.getEncryptedPassword());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(code);
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed password", current.getId());
    }

    public void changeEmail(@Valid ChangeEmailDTO changeEmailDTO) {
        ChangeEmailData changeEmailData = ChangeEmailData.builder()
                .email(changeEmailDTO.getNewEmail())
                .build();
        String data = stringSerializer.serialize(changeEmailData);
        String code = confirmationDomainService.save(data);
        if (with2FA) {
            //TODO: Send email with confirmation code
        }
        log.info("New email changing process has been started: {}", changeEmailData);
        if (!with2FA) {
            changeEmailConfirm(code);
        }
    }

    public void changeEmailConfirm(String code) {
        Confirmation confirmation = confirmationDomainService.getNotExpired(code);
        ChangeEmailData changeEmailData = stringSerializer.deserialize(confirmation.getData(), ChangeEmailData.class);
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        if (current.getEmail().equals(changeEmailData.getEmail())) { //TODO: Move to changeEmail
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailData.getEmail());
        userAuthDomainService.save(current);
        confirmationDomainService.delete(code);
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
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        tokenService.removeAllTokens(userId);
        current.setEnabled(false);
        current.setUsername(UUID.randomUUID().toString() + "_" + current.getUsername());
        current.setGoogleAuth(UUID.randomUUID().toString() + "_" + current.getGoogleAuth());
        userIntegrationService.updateUser(current);
        log.info("User with id '{}' has been disabled", current.getId());
    }
}
