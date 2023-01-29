package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.service.domain.FriendsDomainService;
import com.drsanches.photobooth.app.app.validation.annotation.FriendId;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class FriendIdValidator implements ConstraintValidator<FriendId, String> {

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId == null || userId == null) {
            return true;
        }
        return friendsDomainService.getFriendsIdList(currentUserId).contains(userId);
    }
}
