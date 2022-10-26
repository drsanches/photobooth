package ru.drsanches.photobooth.app.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.validation.annotation.NotCurrentId;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NotCurrentIdValidator implements ConstraintValidator<NotCurrentId, String> {

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return userId == null || tokenSupplier.get() == null || !tokenSupplier.get().getUserId().equals(userId);
    }
}
