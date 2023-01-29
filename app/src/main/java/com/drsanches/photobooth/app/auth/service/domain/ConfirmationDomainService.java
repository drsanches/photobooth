package com.drsanches.photobooth.app.auth.service.domain;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.repository.ConfirmationRepository;
import com.drsanches.photobooth.app.common.exception.auth.WrongConfirmCodeException;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ConfirmationDomainService {

    private static final int CALENDAR_FIELD = GregorianCalendar.MINUTE;

    private static final int CALENDAR_VALUE = 5;

    @Autowired
    private ConfirmationRepository confirmationRepository;

    public Confirmation create(String data, String userId, String email, Operation operation) {
        Confirmation confirmation = new Confirmation();
        confirmation.setId(UUID.randomUUID().toString());
        confirmation.setCode(RandomStringUtils.randomAlphanumeric(6));
        confirmation.setUserId(userId);
        confirmation.setEmail(email);
        confirmation.setOperation(operation);
        confirmation.setData(data);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        confirmation.setExpiresAt(expiresAt);
        Confirmation result = confirmationRepository.save(confirmation);
        log.info("Confirmation created: {}", confirmation);
        return result;
    }

    public Confirmation get(String code) {
        Optional<Confirmation> confirmation = confirmationRepository.findByCode(code);
        if (confirmation.isEmpty()) {
            throw new WrongConfirmCodeException("Wrong confirmation code");
        }
        return confirmation.get();
    }

    public void delete(String id) {
        confirmationRepository.deleteById(id);
        log.info("Confirmation deleted. Id: {}", id);
    }
}
