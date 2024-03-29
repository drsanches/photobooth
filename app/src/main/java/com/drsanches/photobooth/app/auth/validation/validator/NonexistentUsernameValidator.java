package com.drsanches.photobooth.app.auth.validation.validator;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NonexistentUsernameValidator implements ConstraintValidator<NonexistentUsername, String> {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("User with username '" + username + "' already exists")
                .addConstraintViolation();
        return !userAuthDomainService.existsByUsername(username);
    }
}
