package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import com.drsanches.photobooth.app.common.token.UserInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotCurrentIdValidator implements ConstraintValidator<NotCurrentId, String> {

    @Autowired
    private UserInfo userInfo;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(userId) || userInfo.getUserIdOptional()
                .filter(currentUserId -> currentUserId.equals(userId))
                .isEmpty();
    }
}
