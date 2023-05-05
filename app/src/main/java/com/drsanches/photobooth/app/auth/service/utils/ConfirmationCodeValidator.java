package com.drsanches.photobooth.app.auth.service.utils;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.service.domain.ConfirmationDomainService;
import com.drsanches.photobooth.app.common.exception.auth.WrongConfirmCodeException;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;

@Slf4j
@Component
public class ConfirmationCodeValidator {

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    public void validate(Confirmation confirmation, Operation operation) {
        if (confirmation.getUserId() != null && !confirmation.getUserId().equals(tokenSupplier.get().getUserId())
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
