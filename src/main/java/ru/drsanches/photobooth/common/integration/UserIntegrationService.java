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
import ru.drsanches.photobooth.exception.application.UserAlreadyExistsException;
import java.util.Optional;

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

    /**
     * Creates UserAuth and UserProfile in one transaction
     */
    public void createUser(UserAuth userAuth) {
        UserProfile userProfile = new UserProfile();
        copy(userAuth, userProfile);
        try {
            save(userAuth, userProfile);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getGoogleAuth() != null ?
                    userAuth.getGoogleAuth() : userAuth.getUsername(), e);
        }
        log.info("UserAuth and UserProfile has been created: {}, {}", userAuth, userProfile);
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
    private void save(UserAuth userAuth, UserProfile userProfile) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            userAuthRepository.save(userAuth);
            userProfileRepository.save(userProfile);
        });
    }
}
