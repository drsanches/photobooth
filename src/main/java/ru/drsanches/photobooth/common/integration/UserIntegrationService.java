package ru.drsanches.photobooth.common.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.drsanches.photobooth.auth.data.model.UserAuth;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.auth.data.repository.UserAuthRepository;
import ru.drsanches.photobooth.app.data.profile.repository.UserProfileRepository;
import ru.drsanches.photobooth.auth.service.utils.CredentialsHelper;
import ru.drsanches.photobooth.common.token.data.Role;
import ru.drsanches.photobooth.exception.application.UserAlreadyExistsException;
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

    public UserAuth createUser(String username, String password, String email) {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(username.toLowerCase());
        userAuth.setSalt(UUID.randomUUID().toString());
        userAuth.setPassword(credentialsHelper.encodePassword(password, userAuth.getSalt()));
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
            log.info("UserAuth and UserProfile has been created: {}, {}", userAuth, userProfile);
            return result;
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getGoogleAuth() != null ?
                    userAuth.getGoogleAuth() : userAuth.getUsername(), e);
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
            log.error("User profile with id'" + userAuth.getId() + "' does not exist");
        } else {
            userProfile = optionalUserProfile.get();
        }
        copy(userAuth, userProfile);
        save(userAuth, userProfile);
        log.info("UserAuth and UserProfile has been updated: {}, {}", userAuth, userProfile);
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
