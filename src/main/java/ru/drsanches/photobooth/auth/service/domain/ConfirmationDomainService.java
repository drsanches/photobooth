package ru.drsanches.photobooth.auth.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.data.confirmation.model.Confirmation;
import ru.drsanches.photobooth.auth.data.confirmation.model.Operation;
import ru.drsanches.photobooth.auth.data.confirmation.repository.ConfirmationRepository;
import ru.drsanches.photobooth.exception.auth.WrongConfirmCodeException;

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
        confirmation.setCode(UUID.randomUUID().toString());
        confirmation.setUserId(userId);
        confirmation.setEmail(email);
        confirmation.setOperation(operation);
        confirmation.setData(data);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        confirmation.setExpiresAt(expiresAt);
        Confirmation result = confirmationRepository.save(confirmation);
        log.info("Confirmation has been created: {}", confirmation);
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
        log.info("Confirmation with id '{}' has been deleted", id);
    }
}
