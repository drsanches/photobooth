package ru.drsanches.photobooth.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.dto.ChangeEmailDTO;
import ru.drsanches.photobooth.auth.data.dto.ChangePasswordDTO;
import ru.drsanches.photobooth.auth.data.dto.ChangeUsernameDTO;
import ru.drsanches.photobooth.auth.data.dto.LoginDTO;
import ru.drsanches.photobooth.auth.data.dto.RegistrationDTO;
import ru.drsanches.photobooth.auth.data.dto.TokenDTO;
import ru.drsanches.photobooth.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.service.utils.CredentialsHelper;
import ru.drsanches.photobooth.common.token.data.Role;
import ru.drsanches.photobooth.auth.data.mapper.UserAuthInfoMapper;
import ru.drsanches.photobooth.auth.data.model.UserAuth;
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
    private UserAuthInfoMapper userAuthInfoMapper;

    public TokenDTO registration(@Valid RegistrationDTO registrationDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(registrationDTO.getUsername().toLowerCase());
        userAuth.setSalt(UUID.randomUUID().toString());
        userAuth.setPassword(credentialsHelper.encodePassword(registrationDTO.getPassword(), userAuth.getSalt()));
        userAuth.setEmail(registrationDTO.getEmail());
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        userIntegrationService.createUser(userAuth);
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
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        String oldUsername = current.getUsername();
        if (changeUsernameDTO.getNewUsername().equals(oldUsername)) {
            throw new ApplicationException("New username is equal to old");
        }
        current.setUsername(changeUsernameDTO.getNewUsername());
        userIntegrationService.updateUser(current);
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed username from '{}' to '{}'", current.getId(), oldUsername, current.getUsername());
    }

    public void changePassword(@Valid ChangePasswordDTO changePasswordDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        current.setSalt(UUID.randomUUID().toString());
        current.setPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword(), current.getSalt()));
        userAuthDomainService.save(current);
        tokenService.removeAllTokens(userId);
        log.info("User with id '{}' changed password", current.getId());
    }

    public void changeEmail(@Valid ChangeEmailDTO changeEmailDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        if (current.getEmail().equals(changeEmailDTO.getNewEmail())) {
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailDTO.getNewEmail());
        userAuthDomainService.save(current);
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
        userIntegrationService.updateUser(current);
        log.info("User with id '{}' has been disabled", current.getId());
    }
}
