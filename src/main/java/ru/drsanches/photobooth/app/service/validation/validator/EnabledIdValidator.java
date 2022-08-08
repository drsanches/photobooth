package ru.drsanches.photobooth.app.service.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.app.service.validation.annotation.EnabledId;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class EnabledIdValidator implements ConstraintValidator<EnabledId, String> {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return userId == null || userProfileDomainService.enabledExistsById(userId);
    }
}
