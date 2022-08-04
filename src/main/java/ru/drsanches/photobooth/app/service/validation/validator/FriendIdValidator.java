package ru.drsanches.photobooth.app.service.validation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.service.domain.FriendsDomainService;
import ru.drsanches.photobooth.app.service.validation.annotation.FriendId;
import ru.drsanches.photobooth.common.token.TokenSupplier;
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