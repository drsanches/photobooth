package ru.drsanches.photobooth.auth.service.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.exception.auth.WrongPasswordException;

@Component
public class CredentialsHelper {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public void checkPassword(String rawPassword, String encodedPassword, String salt) {
        if (!ENCODER.matches(rawPassword + salt, encodedPassword)) {
            throw new WrongPasswordException();
        }
    }

    public String encodePassword(String password, String salt) {
        return ENCODER.encode(password + salt);
    }
}
