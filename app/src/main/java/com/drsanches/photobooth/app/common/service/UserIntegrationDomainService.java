package com.drsanches.photobooth.app.common.service;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.app.exception.UserAlreadyExistsException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.notifier.data.email.model.EmailInfo;
import com.drsanches.photobooth.app.notifier.data.email.repository.EmailInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * Service for updating UserAuth, UserProfile and EmailInfo objects together
 */
@Slf4j
@Service
@Deprecated
//TODO: Delete
public class UserIntegrationDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private EmailInfoRepository emailInfoRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

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
        var userProfile = new UserProfile();
        copy(userAuth, userProfile);
        var emailInfo = new EmailInfo(userAuth.getId(), userAuth.getEmail());
        try {
            var savedUserAuth = update(
                    userAuth,
                    userProfile,
                    false,
                    emailInfo
            );
            log.debug("UserAuth with UserProfile and EmailInfo created. UserAuth: {}, UserProfile: {}, EmailInfo: {}",
                    userAuth, userProfile, emailInfo);
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
        var userAuth  = userAuthRepository.findById(userId).orElseThrow(() -> new NoUserIdException(userId));
        userAuth.setUsername(username);
        var userProfile = userProfileRepository.findById(userAuth.getId())
                .orElseGet(() -> {
                    log.error("UserProfile does not exist. Id: {}", userAuth.getId());
                    return new UserProfile();
                });
        copy(userAuth, userProfile);
        try {
            update(
                    userAuth,
                    userProfile,
                    false,
                    null
            );
            log.debug("UserAuth with UserProfile updated. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
        }
    }

    public void updateEmail(String userId, String email) {
        var userAuth  = userAuthRepository.findById(userId).orElseThrow(() -> new NoUserIdException(userId));
        userAuth.setEmail(email);
        var emailInfo = new EmailInfo(userAuth.getId(), userAuth.getEmail());
        try {
            update(
                    userAuth,
                    null,
                    true,
                    emailInfo
            );
            log.debug("UserAuth with EmailInfo updated. UserAuth: {}, EmailInfo: {}", userAuth, emailInfo);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
        }
    }

    public void disableUser(String userId) {
        var userAuth = userAuthRepository.findById(userId).map(it -> it.toBuilder()
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
        var userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    log.error("UserProfile does not exist. Id: {}", userAuth.getId());
                    return new UserProfile();
                });
        copy(userAuth, userProfile);
        try {
            update(
                    userAuth,
                    userProfile,
                    true,
                    null
            );
            log.debug("UserAuth with UserProfile disabled. All NotificationInfo objects removed. " +
                    "UserAuth: {}, UserProfile: {}", userAuth, userProfile);
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
     * Updates UserAuth, UserProfile and NotificationInfo in one transaction
     * @param userAuth required
     * @param userProfile null for ignore, creates/updates if exists
     * @param deleteEmailInfo true for emailInfo deletion
     * @param emailInfo null for ignore, creates new if exists
     * @return Saved UserAuth
     */
    private UserAuth update(
            UserAuth userAuth,
            @Nullable UserProfile userProfile,
            boolean deleteEmailInfo,
            @Nullable EmailInfo emailInfo
    ) {
        return new TransactionTemplate(transactionManager).execute(status -> {
            var savedUserAuth = userAuthRepository.save(userAuth);
            if (userProfile != null) {
                userProfileRepository.save(userProfile);
            }
            if (deleteEmailInfo) {
                try {
//                    emailInfoRepository.deleteByIdUserId(userAuth.getId());
                } catch (EmptyResultDataAccessException e) {
                    log.warn("EmailInfo does not exist. UserId: {}", userAuth.getId(), e);
                }
            }
            if (emailInfo != null) {
                emailInfoRepository.save(emailInfo);
            }
            return savedUserAuth;
        });
    }
}
