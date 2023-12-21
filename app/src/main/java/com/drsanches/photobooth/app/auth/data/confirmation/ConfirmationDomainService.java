package com.drsanches.photobooth.app.auth.data.confirmation;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.repository.ConfirmationRepository;
import com.drsanches.photobooth.app.auth.exception.WrongConfirmCodeException;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfirmationDomainService {

    private static final int CALENDAR_FIELD = Calendar.MINUTE;

    private static final int CALENDAR_VALUE = 5;

    @Autowired
    private ConfirmationRepository confirmationRepository;

    @Autowired
    private ConfirmationCodeGenerator confirmationCodeGenerator;

    public Confirmation create(@Nullable String data, @Nullable String userId, Operation operation) {
        var expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        var savedConfirmation = confirmationRepository.save(Confirmation.builder()
                .id(UUID.randomUUID().toString())
                .code(confirmationCodeGenerator.generate()) //TODO: Use as id?
                .userId(userId)
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

    public List<Confirmation> getExpired() {
        return confirmationRepository.findByExpiresAtLessThan(new GregorianCalendar());
    }

    public void delete(String id) {
        confirmationRepository.deleteById(id);
        log.debug("Confirmation deleted. Id: {}", id);
    }

    public void deleteAll(List<Confirmation> confirmations) {
        confirmationRepository.deleteAll(confirmations);
        log.debug("Confirmations deleted: {}", confirmations);
    }
}
