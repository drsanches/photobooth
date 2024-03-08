package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.FriendId;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FriendIdValidator implements ConstraintValidator<FriendId, String> {

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private AuthInfo authInfo;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        var currentUserId = authInfo.getUserId();
        if (currentUserId == null || userId == null) {
            return true;
        }
        return friendsDomainService.getFriendsIds(currentUserId).contains(userId);
    }
}
