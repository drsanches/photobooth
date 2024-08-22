package com.drsanches.photobooth.app.auth.data.userauth;

import com.drsanches.photobooth.app.app.exception.UserNotFoundException;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.userauth.repository.UserAuthRepository;
import com.drsanches.photobooth.app.auth.data.token.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserAuthDomainService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    public UserAuth createUser(String username, String email, String encryptedPassword, String salt) {
        return createUser(username, email, encryptedPassword, salt, Role.USER);
    }

    public UserAuth createAdmin(String username, String email, String encryptedPassword, String salt) {
        return createUser(username, email, encryptedPassword, salt, Role.ADMIN);
    }

    private UserAuth createUser(String username, String email, String encryptedPassword, String salt, Role role) {
        var userAuth = UserAuth.builder()
                .id(UUID.randomUUID().toString())
                .username(username.toLowerCase())
                .password(encryptedPassword)
                .salt(salt)
                .email(email)
                .enabled(true)
                .role(role)
                .build();
        userAuthRepository.save(userAuth);
        log.debug("UserAuth created: {}", userAuth);
        return userAuth;
    }

    public UserAuth createUserByGoogle(String googleEmail) {
        var userAuth = UserAuth.builder()
                .id(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .email(googleEmail)
                .googleAuth(googleEmail)
                .enabled(true)
                .role(Role.USER)
                .build();
        userAuthRepository.save(userAuth);
        log.debug("UserAuth created by Google: {}", userAuth);
        return userAuth;
    }

    public void updateUsername(String userId, String username) {
        var userAuth = getEnabledById(userId);
        userAuth.setUsername(username);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth username updated: {}", userAuth);
    }

    public void updatePassword(String userId, String password, String salt) {
        var userAuth = getEnabledById(userId);
        userAuth.setPassword(password);
        userAuth.setSalt(salt);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth password updated: {}", userAuth);
    }

    public void updateEmail(String userId, String email) {
        var userAuth = getEnabledById(userId);
        userAuth.setEmail(email);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth email updated: {}", userAuth);
    }

    public void setGoogleAuth(String userId, String googleAuth) {
        var userAuth = getEnabledById(userId);
        userAuth.setGoogleAuth(googleAuth);
        userAuthRepository.save(userAuth);
        log.debug("UserAuth googleAuth set: {}", userAuth);
    }

    public Optional<UserAuth> findEnabledById(String userId) {
        return userAuthRepository.findById(userId)
                .filter(UserAuth::isEnabled);
    }

    public UserAuth getEnabledById(String userId) {
        return findEnabledById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<UserAuth> findEnabledByUsername(String username) {
        return userAuthRepository.findByUsernameAndEnabled(username, true);
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

    public void disableUser(String userId) {
        var userAuth = getEnabledById(userId);
        userAuth = userAuth.toBuilder()
                .enabled(false)
                .username(UUID.randomUUID() + "_" + userAuth.getUsername())
                .email(UUID.randomUUID() + "_" + userAuth.getEmail())
                .googleAuth(UUID.randomUUID() + "_" + userAuth.getGoogleAuth())
                .build();
        userAuthRepository.save(userAuth);
        log.debug("UserAuth disabled: {}", userAuth);
    }
}
