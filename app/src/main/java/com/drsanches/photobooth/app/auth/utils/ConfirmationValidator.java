package com.drsanches.photobooth.app.auth.utils;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;

@Slf4j
@Component
public class ConfirmationValidator {

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    public void validate(Confirmation confirmation, Operation operation) {
        if (operation != confirmation.getOperation()) {
            throw new WrongConfirmCodeException();
        }
        validate(confirmation);
    }

    public void validate(Confirmation confirmation) {
        if (confirmation.getExpires().before(new GregorianCalendar())) {
            confirmationDomainService.delete(confirmation.getId());
            log.info("Expired Confirmation deleted: {}", confirmation);
            throw new WrongConfirmCodeException();
        }
    }
}
