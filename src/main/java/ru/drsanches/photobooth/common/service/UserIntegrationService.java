package ru.drsanches.photobooth.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.auth.data.userauth.repository.UserAuthRepository;
import ru.drsanches.photobooth.app.data.profile.repository.UserProfileRepository;
import ru.drsanches.photobooth.auth.service.utils.CredentialsHelper;
import ru.drsanches.photobooth.common.token.data.Role;
import ru.drsanches.photobooth.exception.application.UserAlreadyExistsException;
import ru.drsanches.photobooth.exception.server.ServerError;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for updating UserAuth and UserProfile objects together
 */
@Slf4j
@Service
public class UserIntegrationService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private CredentialsHelper credentialsHelper;

    public UserAuth createUser(String username, String email, String encryptedPassword, String salt) {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(username.toLowerCase());
        userAuth.setPassword(encryptedPassword);
        userAuth.setSalt(salt);
        userAuth.setEmail(email);
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        return createUser(userAuth);
    }

    public UserAuth createUserByGoogle(String googleEmail) {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(UUID.randomUUID().toString());
        userAuth.setEmail(googleEmail);
        userAuth.setGoogleAuth(googleEmail);
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        return createUser(userAuth);
    }

    /**
     * Creates UserAuth and UserProfile in one transaction
     */
    public UserAuth createUser(UserAuth userAuth) {
        UserProfile userProfile = new UserProfile();
        copy(userAuth, userProfile);
        try {
            UserAuth result = save(userAuth, userProfile);
            log.info("UserAuth with UserProfile created. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
            return result;
        } catch(DataIntegrityViolationException e) {
            if (userAuth.getGoogleAuth() != null) {
                throw new UserAlreadyExistsException(userAuth.getGoogleAuth(), e);
            } else {
                throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
            }
        }
    }

    /**
     * Updates UserAuth and UserProfile in one transaction
     */
    public void updateUser(UserAuth userAuth) {
        Optional<UserProfile> optionalUserProfile = userProfileRepository.findById(userAuth.getId());
        UserProfile userProfile;
        if (optionalUserProfile.isEmpty()) {
            userProfile = new UserProfile();
            log.error("UserProfile does not exist. Id: {}", userAuth.getId());
        } else {
            userProfile = optionalUserProfile.get();
        }
        copy(userAuth, userProfile);
        try {
            save(userAuth, userProfile);
            log.info("UserAuth with UserProfile updated. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
        }
    }

    /**
     * Updates UserAuth and UserProfile in one transaction
     */
    public void disableUser(String userId) {
        Optional<UserAuth> optionalUserAuth = userAuthRepository.findById(userId);
        UserAuth userAuth;
        if (optionalUserAuth.isPresent()) {
            userAuth = optionalUserAuth.get();
            userAuth.setEnabled(false);
            userAuth.setUsername(UUID.randomUUID().toString() + "_" + userAuth.getUsername());
            userAuth.setEmail(UUID.randomUUID().toString() + "_" + userAuth.getEmail());
            userAuth.setGoogleAuth(UUID.randomUUID().toString() + "_" + userAuth.getGoogleAuth());
        } else {
            log.error("UserAuth does not exist. Id: {}", userId);
            userAuth = new UserAuth();
            userAuth.setId(userId);
            userAuth.setUsername(UUID.randomUUID().toString());
            userAuth.setEmail(UUID.randomUUID().toString());
            userAuth.setEnabled(false);
            userAuth.setRole(Role.USER);
        }
        Optional<UserProfile> optionalUserProfile = userProfileRepository.findById(userId);
        UserProfile userProfile;
        if (optionalUserProfile.isPresent()) {
            userProfile = optionalUserProfile.get();
        } else {
            userProfile = new UserProfile();
            log.error("UserProfile does not exist. Id: {}", userAuth.getId());
        }
        copy(userAuth, userProfile);
        try {
            save(userAuth, userProfile);
            log.info("UserAuth with UserProfile disabled. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new ServerError("User disable error", e);
        }
    }

    /**
     * Fills UserProfile with data from UserAuth
     */
    private void copy(UserAuth source, UserProfile target) {
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setEnabled(source.isEnabled());
    }

    /**
     * Saves UserAuth and UserProfile objects in one transaction
     */
    private UserAuth save(UserAuth userAuth, UserProfile userProfile) {
        return new TransactionTemplate(transactionManager).execute(status -> {
            UserAuth result = userAuthRepository.save(userAuth);
            userProfileRepository.save(userProfile);
            return result;
        });
    }
}
