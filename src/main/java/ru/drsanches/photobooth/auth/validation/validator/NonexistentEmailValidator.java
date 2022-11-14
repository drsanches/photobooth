package ru.drsanches.photobooth.auth.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.service.domain.UserAuthDomainService;
import ru.drsanches.photobooth.auth.validation.annotation.NonexistentEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NonexistentEmailValidator implements ConstraintValidator<NonexistentEmail, String> {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("User with email '" + email + "' already exists")
                .addConstraintViolation();
        return !userAuthDomainService.existsByEmail(email);
    }
}
