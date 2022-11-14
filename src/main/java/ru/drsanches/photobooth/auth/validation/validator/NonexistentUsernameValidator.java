package ru.drsanches.photobooth.auth.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.service.domain.UserAuthDomainService;
import ru.drsanches.photobooth.auth.validation.annotation.NonexistentUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
