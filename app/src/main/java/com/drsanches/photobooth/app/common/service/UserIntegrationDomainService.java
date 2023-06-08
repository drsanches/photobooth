package com.drsanches.photobooth.app.common.service;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.auth.service.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.exception.application.UserAlreadyExistsException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * Service for updating UserAuth and UserProfile objects together
 */
@Slf4j
@Service
public class UserIntegrationDomainService {

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
            log.debug("UserAuth with UserProfile created. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
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
        UserProfile userProfile = userProfileRepository.findById(userAuth.getId())
                .orElseGet(() -> {
                    log.error("UserProfile does not exist. Id: {}", userAuth.getId());
                    return new UserProfile();
                });
        copy(userAuth, userProfile);
        try {
            save(userAuth, userProfile);
            log.debug("UserAuth with UserProfile updated. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
        }
    }

    /**
     * Updates UserAuth and UserProfile in one transaction
     */
    public void disableUser(String userId) {
        UserAuth userAuth = userAuthRepository.findById(userId).map(it -> {
            it.setEnabled(false);
            it.setUsername(UUID.randomUUID() + "_" + it.getUsername());
            it.setEmail(UUID.randomUUID() + "_" + it.getEmail());
            it.setGoogleAuth(UUID.randomUUID() + "_" + it.getGoogleAuth());
            return it;
        }).orElseGet(() -> {
            log.error("UserAuth does not exist. Id: {}", userId);
            UserAuth result = new UserAuth();
            result.setId(userId);
            result.setUsername(UUID.randomUUID().toString());
            result.setEmail(UUID.randomUUID().toString());
            result.setEnabled(false);
            result.setRole(Role.USER);
            return result;
        });
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                        log.error("UserProfile does not exist. Id: {}", userAuth.getId());
                        return new UserProfile();
                });
        copy(userAuth, userProfile);
        try {
            save(userAuth, userProfile);
            log.debug("UserAuth with UserProfile disabled. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
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
