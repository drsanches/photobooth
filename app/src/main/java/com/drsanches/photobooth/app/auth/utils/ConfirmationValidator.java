package com.drsanches.photobooth.app.auth.utils;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import com.drsanches.photobooth.app.common.token.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;

@Slf4j
@Component
public class ConfirmationValidator {

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    public void validate(Confirmation confirmation, Operation operation) {
        if (confirmation.getUserId() != null && !confirmation.getUserId().equals(userInfo.getUserId())
                || operation != confirmation.getOperation()) {
            throw new WrongConfirmCodeException();
        }
        if (confirmation.getExpiresAt().before(new GregorianCalendar())) {
            confirmationDomainService.delete(confirmation.getId());
            log.info("Expired Confirmation deleted: {}", confirmation);
            throw new WrongConfirmCodeException("Confirmation code expired");
        }
    }
}
