package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.FriendIds;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Component
public class FriendIdsValidator implements ConstraintValidator<FriendIds, Collection<String>> {

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Override
    public boolean isValid(Collection<String> userIds, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        List<String> friendIds = friendsDomainService.getFriendsIdList(tokenSupplier.get().getUserId());
        return friendIds.containsAll(userIds);
    }
}
