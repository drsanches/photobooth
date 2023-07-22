package com.drsanches.photobooth.app.common.service;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.app.exception.UserAlreadyExistsException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.common.token.data.model.Role;
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
        return createUser(username, email, encryptedPassword, salt, Role.USER);
    }

    public UserAuth createAdmin(String username, String email, String encryptedPassword, String salt) {
        return createUser(username, email, encryptedPassword, salt, Role.ADMIN);
    }

    private UserAuth createUser(String username, String email, String encryptedPassword, String salt, Role role) {
        return createUser(UserAuth.builder()
                .id(UUID.randomUUID().toString())
                .username(username.toLowerCase())
                .password(encryptedPassword)
                .salt(salt)
                .email(email)
                .enabled(true)
                .role(role)
                .build());
    }

    public UserAuth createUserByGoogle(String googleEmail) {
        return createUser(UserAuth.builder()
                .id(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .email(googleEmail)
                .googleAuth(googleEmail)
                .enabled(true)
                .role(Role.USER)
                .build());
    }

    private UserAuth createUser(UserAuth userAuth) {
        UserProfile userProfile = new UserProfile();
        copy(userAuth, userProfile);
        try {
            UserAuth savedUserAuth = save(userAuth, userProfile);
            log.debug("UserAuth with UserProfile created. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
            return savedUserAuth;
        } catch(DataIntegrityViolationException e) {
            if (userAuth.getGoogleAuth() != null) {
                throw new UserAlreadyExistsException(userAuth.getGoogleAuth(), e);
            } else {
                throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
            }
        }
    }

    public void updateUsername(String userId, String username) {
        UserAuth userAuth  = userAuthRepository.findById(userId).orElseThrow(() -> new NoUserIdException(userId));
        userAuth.setUsername(username);
        updateUser(userAuth);
    }

    private void updateUser(UserAuth userAuth) {
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

    public void disableUser(String userId) {
        UserAuth userAuth = userAuthRepository.findById(userId).map(it -> it.toBuilder()
                .enabled(false)
                .username(UUID.randomUUID() + "_" + it.getUsername())
                .email(UUID.randomUUID() + "_" + it.getEmail())
                .googleAuth(UUID.randomUUID() + "_" + it.getGoogleAuth())
                .build()
        ).orElseGet(() -> {
            log.error("UserAuth does not exist. Id: {}", userId);
            return UserAuth.builder()
                    .id(userId)
                    .username(UUID.randomUUID().toString())
                    .email(UUID.randomUUID().toString())
                    .enabled(false)
                    .role(Role.USER)
                    .build();
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
            UserAuth savedUserAuth = userAuthRepository.save(userAuth);
            userProfileRepository.save(userProfile);
            return savedUserAuth;
        });
    }
}
