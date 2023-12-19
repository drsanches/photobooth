package com.drsanches.photobooth.app.auth.validation.validator;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NonexistentEmailValidator implements ConstraintValidator<NonexistentEmail, String> {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("User with email '" + email + "' already exists")
                .addConstraintViolation();
        //TODO: Check googleAuth?
        return !userAuthDomainService.existsByEmail(email);
    }
}
