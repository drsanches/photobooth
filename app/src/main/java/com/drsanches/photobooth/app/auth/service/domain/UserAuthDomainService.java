package com.drsanches.photobooth.app.auth.service.domain;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.common.exception.application.NoUserIdException;
import com.drsanches.photobooth.app.common.exception.application.NoUsernameException;
import com.drsanches.photobooth.app.common.exception.auth.NoGoogleUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserAuthDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    public void save(UserAuth userAuth) {
        userAuthRepository.save(userAuth);
        log.info("UserAuth updated: {}", userAuth);
    }

    public UserAuth getEnabledById(String userId) {
        Optional<UserAuth> user = userAuthRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new NoUserIdException(userId);
        }
        return user.get();
    }

    public UserAuth getEnabledByUsername(String username) {
        Optional<UserAuth> user = userAuthRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new NoUsernameException(username);
        }
        return user.get();
    }

    public boolean existsByUsername(String username) {
        return userAuthRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    public UserAuth getEnabledByGoogleAuth(String googleAuth) {
        Optional<UserAuth> user = userAuthRepository.findByGoogleAuth(googleAuth);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new NoGoogleUserException();
        }
        return user.get();
    }
}
