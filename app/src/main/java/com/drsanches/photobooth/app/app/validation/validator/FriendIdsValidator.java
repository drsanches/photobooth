package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.config.UserInfo;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.FriendIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Component
public class FriendIdsValidator implements ConstraintValidator<FriendIds, Collection<String>> {

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Override
    public boolean isValid(Collection<String> userIds, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        var friendIds = friendsDomainService.getFriendsIds(userInfo.getUserId());
        return friendIds.containsAll(userIds);
    }
}
