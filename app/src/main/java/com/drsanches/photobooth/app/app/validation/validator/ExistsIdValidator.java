package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.service.domain.UserProfileDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.ExistsId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ExistsIdValidator implements ConstraintValidator<ExistsId, String> {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(userId) || userProfileDomainService.anyExistsById(userId);
    }
}
