package ru.drsanches.photobooth.auth.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;
import ru.drsanches.photobooth.auth.data.userauth.repository.UserAuthRepository;
import ru.drsanches.photobooth.common.integration.UserIntegrationService;
import ru.drsanches.photobooth.common.token.data.Role;
import ru.drsanches.photobooth.common.utils.Initializer;

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
    private UserIntegrationService userIntegrationService;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Override
    public void initialize() {
        if (userAuthRepository.findByUsername(username).isPresent()) {
            log.info("Admin with username '{}' is already initialized", username);
            return;
        }
        try {
            UserAuth userAuth = new UserAuth();
            userAuth.setId(UUID.randomUUID().toString());
            userAuth.setUsername(username);
            userAuth.setEmail("admin@example.com"); //TODO: Use real email and move it to env?
            userAuth.setSalt(UUID.randomUUID().toString());
            userAuth.setPassword(new BCryptPasswordEncoder().encode(sha256(password) + userAuth.getSalt()));
            userAuth.setEnabled(true);
            userAuth.setRole(Role.ADMIN);
            userIntegrationService.createUser(userAuth);
            log.info("Admin with id '{}' has been initialized", userAuth.getId());
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
