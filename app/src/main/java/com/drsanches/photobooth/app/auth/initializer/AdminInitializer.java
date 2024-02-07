package com.drsanches.photobooth.app.auth.initializer;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.initializer.Initializer;
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

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public void initialize() {
        if (userAuthDomainService.existsByUsername(username)) {
            log.info("Admin already initialized. Username: {}", username);
            return;
        }
        try {
            var salt = UUID.randomUUID().toString();
            var savedUserAuth = userAuthDomainService.createAdmin(
                    username,
                    "admin@example.com", //TODO: Use env or yaml?
                    new BCryptPasswordEncoder().encode(sha256(password) + salt),
                    salt
            );
            log.info("Admin initialized. Id: {}", savedUserAuth.getId());
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate sha256 hash", e);
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
