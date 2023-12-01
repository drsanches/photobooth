package com.drsanches.photobooth.app.common.service;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.app.exception.UserAlreadyExistsException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.notifier.data.model.NotificationInfo;
import com.drsanches.photobooth.app.notifier.data.model.NotificationType;
import com.drsanches.photobooth.app.notifier.data.repository.NotificationInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Service for updating UserAuth, UserProfile and NotificationInfo objects together
 */
@Slf4j
@Service
public class UserIntegrationDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private NotificationInfoRepository notificationInfoRepository;

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
        UserProfile userProfile = new UserProfile();
        copy(userAuth, userProfile);
        NotificationInfo notificationInfo = createNotificationInfo(userAuth);
        try {
            UserAuth savedUserAuth = update(
                    userAuth,
                    userProfile,
                    List.of(NotificationType.values()),
                    notificationInfo
            );
            log.debug("UserAuth with UserProfile and NotificationInfo created. " +
                    "Other NotificationInfo objects removed. " +
                    "UserAuth: {}, UserProfile: {}, NotificationInfo: {}",
                    userAuth, userProfile, notificationInfo);
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
        UserProfile userProfile = userProfileRepository.findById(userAuth.getId())
                .orElseGet(() -> {
                    log.error("UserProfile does not exist. Id: {}", userAuth.getId());
                    return new UserProfile();
                });
        copy(userAuth, userProfile);
        try {
            update(
                    userAuth,
                    userProfile,
                    null,
                    null
            );
            log.debug("UserAuth with UserProfile updated. UserAuth: {}, UserProfile: {}", userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), userAuth.getEmail(), e);
        }
    }

    public void updateEmail(String userId, String email) {
        UserAuth userAuth  = userAuthRepository.findById(userId).orElseThrow(() -> new NoUserIdException(userId));
        userAuth.setEmail(email);
        NotificationInfo notificationInfo = createNotificationInfo(userAuth);
        try {
            update(
                    userAuth,
                    null,
                    List.of(NotificationType.EMAIL),
                    notificationInfo
            );
            log.debug("UserAuth with NotificationInfo updated. " +
                    "Other NotificationInfo objects with type {} removed. " +
                    "UserAuth: {}, NotificationInfo: {}",
                    NotificationType.EMAIL, userAuth, notificationInfo);
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
            update(
                    userAuth,
                    userProfile,
                    List.of(NotificationType.values()),
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

    private NotificationInfo createNotificationInfo(UserAuth userAuth) {
        return new NotificationInfo(
                UUID.randomUUID().toString(),
                userAuth.getId(),
                userAuth.getEmail(),
                NotificationType.EMAIL
        );
    }

    /**
     * Updates UserAuth, UserProfile and NotificationInfo in one transaction
     * @param userAuth required
     * @param userProfile null for ignore, creates/updates if exists
     * @param notificationInfo null for ignore, creates new if exists
     * @param notificationTypesToRemove removes all notificationInfo objects with notificationType from list
     * @return Saved UserAuth
     */
    private UserAuth update(
            UserAuth userAuth,
            @Nullable UserProfile userProfile,
            @Nullable List<NotificationType> notificationTypesToRemove,
            @Nullable NotificationInfo notificationInfo
    ) {
        return new TransactionTemplate(transactionManager).execute(status -> {
            UserAuth savedUserAuth = userAuthRepository.save(userAuth);
            if (userProfile != null) {
                userProfileRepository.save(userProfile);
            }
            if (notificationTypesToRemove != null) {
                for (NotificationType type: notificationTypesToRemove) {
                    notificationInfoRepository.deleteByUserIdAndType(userAuth.getId(), type);
                }
            }
            if (notificationInfo != null) {
                notificationInfoRepository.save(notificationInfo);
            }
            return savedUserAuth;
        });
    }
}
