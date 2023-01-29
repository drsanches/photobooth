package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
