package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.validation.annotation.UserId;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserIdValidator implements ConstraintValidator<UserId, Object> {

    @Autowired
    private UserProfileDomainService userProfileDomainService;
    @Autowired
    private FriendsDomainService friendsDomainService;
    @Autowired
    private AuthInfo authInfo;

    List<UserId.Violation> violations;

    @Override
    public void initialize(UserId constraintAnnotation) {
        this.violations = List.of(constraintAnnotation.violations());
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Set<String> friendIds = null;
        if (authInfo.isAuthorized() && violations.contains(UserId.Violation.FRIEND)) {
            friendIds = friendsDomainService.getFriendsIds(authInfo.getUserId());
        }
        Set<UserId.Violation> badViolationResults;
        if (object == null) {
            return true;
        }
        if (object instanceof String) {
            badViolationResults = getBadValidationResults((String) object, friendIds);
        } else if (object instanceof Collection<?>) {
            badViolationResults = getBadValidationResults((Collection<String>) object, friendIds); //TODO
        } else {
            throw new ServerError("Wrong validation object");
        }

        if (!badViolationResults.isEmpty()) {
            context.disableDefaultConstraintViolation();
            badViolationResults.forEach(it -> context
                    .buildConstraintViolationWithTemplate(it.getMessage())
                    .addConstraintViolation());
            return false;
        }
        return true;
    }

    private Set<UserId.Violation> getBadValidationResults(Collection<String> userIds, Set<String> friendIds) {
        Set<UserId.Violation> badViolationResults = new HashSet<>();
        userIds.forEach(userId -> badViolationResults.addAll(getBadValidationResults(userId, friendIds)));
        return badViolationResults;
    }

    private Set<UserId.Violation> getBadValidationResults(String userId, @Nullable Set<String> friendIds) {
        if (StringUtils.isEmpty(userId)) {
            return Set.of();
        }
        UserProfile userProfile = null;
        if (violations.contains(UserId.Violation.FRIEND) && violations.size() > 1
                || !violations.contains(UserId.Violation.FRIEND) && violations.size() > 0) {
            userProfile = userProfileDomainService.findById(userId).orElse(null);
        }
        return getBadValidationResults(userProfile, friendIds);
    }

    private Set<UserId.Violation> getBadValidationResults(
            @Nullable UserProfile userProfile,
            @Nullable Set<String> friendIds
    ) {
        Set<UserId.Violation> badViolationResults = new HashSet<>();
        violations.forEach(violation -> {
            if (!isValid(userProfile, friendIds, violation)) {
                badViolationResults.add(violation);
            }
        });
        return badViolationResults;
    }

    private boolean isValid(
            @Nullable UserProfile userProfile,
            @Nullable Set<String> friendIds,
            UserId.Violation violation
    ) {
        return switch (violation) {
            case ENABLED_OR_DISABLED -> userProfile != null;
            case ENABLED -> userProfile != null && userProfile.isEnabled();
            case FRIEND -> userProfile != null && friendIds != null && friendIds.contains(userProfile.getId());
            case NOT_CURRENT -> userProfile == null || authInfo.getUserIdOptional()
                    .filter(currentUserId -> currentUserId.equals(userProfile.getId()))
                    .isEmpty();
        };
    }
}
