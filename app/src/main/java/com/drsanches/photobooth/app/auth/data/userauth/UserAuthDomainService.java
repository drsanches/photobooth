package com.drsanches.photobooth.app.auth.data.userauth;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import com.drsanches.photobooth.app.auth.exception.NoGoogleUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAuthDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    public void updatePassword(String userId, String password, String salt) {
        UserAuth userAuth = getEnabledById(userId);
        userAuth.setPassword(password);
        userAuth.setSalt(salt);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth password updated: {}", userAuth);
    }

    public UserAuth getEnabledById(String userId) {
        return userAuthRepository.findById(userId)
                .filter(UserAuth::isEnabled)
                .orElseThrow(() -> new NoUserIdException(userId));
    }

    public UserAuth getEnabledByUsername(String username) {
        return userAuthRepository.findByUsername(username)
                .filter(UserAuth::isEnabled)
                .orElseThrow(() -> new NoUsernameException(username));
    }

    public boolean existsByUsername(String username) {
        return userAuthRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    public UserAuth getEnabledByGoogleAuth(String googleAuth) {
        return userAuthRepository.findByGoogleAuth(googleAuth)
                .filter(UserAuth::isEnabled)
                .orElseThrow(NoGoogleUserException::new);
    }
}
