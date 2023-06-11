package com.drsanches.photobooth.app.auth.data.confirmation;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.repository.ConfirmationRepository;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

@Slf4j
@Component
public class ConfirmationDomainService {

    private static final int CALENDAR_FIELD = Calendar.MINUTE;

    private static final int CALENDAR_VALUE = 5;

    @Autowired
    private ConfirmationRepository confirmationRepository;

    public Confirmation create(String data, String userId, String email, Operation operation) {
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        Confirmation savedConfirmation = confirmationRepository.save(Confirmation.builder()
                .id(UUID.randomUUID().toString())
                .code(RandomStringUtils.randomAlphanumeric(6))
                .userId(userId)
                .email(email)
                .operation(operation)
                .data(data)
                .expiresAt(expiresAt)
                .build());
        log.debug("Confirmation created: {}", savedConfirmation);
        return savedConfirmation;
    }

    public Confirmation get(String code) {
        return confirmationRepository.findByCode(code)
                .orElseThrow(() -> new WrongConfirmCodeException("Wrong confirmation code"));
    }

    public void delete(String id) {
        confirmationRepository.deleteById(id);
        log.debug("Confirmation deleted. Id: {}", id);
    }
}
