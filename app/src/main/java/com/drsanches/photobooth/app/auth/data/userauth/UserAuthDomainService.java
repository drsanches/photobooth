package com.drsanches.photobooth.app.auth.data.userauth;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserAuthDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    public void updatePassword(String userId, String password, String salt) {
        var userAuth = getEnabledById(userId);
        userAuth.setPassword(password);
        userAuth.setSalt(salt);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth password updated: {}", userAuth);
    }

    public void setGoogleAuth(String userId, String googleAuth) {
        var userAuth = getEnabledById(userId);
        userAuth.setGoogleAuth(googleAuth);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth googleAuth set: {}", userAuth);
    }

    public UserAuth getEnabledById(String userId) {
        return userAuthRepository.findById(userId)
                .filter(UserAuth::isEnabled)
                .orElseThrow(() -> new NoUserIdException(userId));
    }

    public UserAuth getEnabledByUsername(String username) {
        return userAuthRepository.findByUsernameAndEnabled(username, true)
                .orElseThrow(() -> new NoUsernameException(username));
    }

    public Optional<UserAuth> findEnabledByEmail(String email) {
        return userAuthRepository.findByEmailAndEnabled(email, true);
    }

    public Optional<UserAuth> findEnabledByGoogleAuth(String googleAuth) {
        return userAuthRepository.findByGoogleAuthAndEnabled(googleAuth, true);
    }

    public boolean existsByUsername(String username) {
        return userAuthRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    public boolean existsByGoogleAuth(String googleAuth) {
        return userAuthRepository.existsByGoogleAuth(googleAuth);
    }
}
