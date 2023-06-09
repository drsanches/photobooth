package com.drsanches.photobooth.app.initializer;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.common.exception.BaseException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Component
public class AdminInitializer implements Initializer {

    @Value("${application.admin.username}")
    private String username;

    @Value("${application.admin.password}")
    private String password;

    @Value(value = "${spring.mail.username:admin@example.com}")
    private String email;

    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public void initialize() {
        if (userAuthDomainService.existsByUsername(username)) {
            log.info("Admin already initialized. Username: {}", username);
            return;
        }
        try {
            String salt = UUID.randomUUID().toString();
            UserAuth savedUserAuth = userIntegrationDomainService.createUser(UserAuth.builder()
                    .id(UUID.randomUUID().toString())
                    .username(username)
                    .email(email)
                    .salt(salt)
                    .password(new BCryptPasswordEncoder().encode(sha256(password) + salt))
                    .enabled(true)
                    .role(Role.ADMIN)
                    .build());
            log.info("Admin initialized. Id: {}", savedUserAuth.getId());
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate sha256 hash. Exception: {}", BaseException.log(e));
            System.exit(1);
        }
    }

    private String sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
