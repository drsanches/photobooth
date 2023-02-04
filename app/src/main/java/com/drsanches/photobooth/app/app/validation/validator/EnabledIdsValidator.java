package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.service.domain.UserProfileDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.EnabledIds;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnabledIdsValidator implements ConstraintValidator<EnabledIds, Collection<String>> {

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Override
    public boolean isValid(Collection<String> userIds, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        List<String> enabledIds = userProfileDomainService.getEnabledByIds(userIds).stream()
                .map(UserProfile::getId)
                .collect(Collectors.toList());
        return enabledIds.containsAll(userIds);
    }
}