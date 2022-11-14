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

    public Confirmation create(String data, String userId, Operation operation) {
        Confirmation confirmation = new Confirmation();
        confirmation.setId(UUID.randomUUID().toString());
        confirmation.setCode(UUID.randomUUID().toString());
        confirmation.setUserId(userId);
        confirmation.setOperation(operation);
        confirmation.setData(data);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        confirmation.setExpiresAt(expiresAt);
        Confirmation result = confirmationRepository.save(confirmation);
        log.info("Confirmation has been created: {}", confirmation);
        return result;
    }

    public Confirmation getNotExpired(String code) {
        Optional<Confirmation> optionalConfirmation = confirmationRepository.findByCode(code);
        if (optionalConfirmation.isEmpty()) {
            throw new WrongConfirmCodeException("Wrong confirmation code");
        }
        Confirmation confirmation = optionalConfirmation.get();
        if (confirmation.getExpiresAt().before(new GregorianCalendar())) {
            confirmationRepository.delete(confirmation);
            log.info("Expired Confirmation has been deleted: {}", confirmation);
            throw new WrongConfirmCodeException("Confirmation code has been expired");
        }
        return confirmation;
    }

    public void delete(String id) {
        confirmationRepository.deleteById(id);
        log.info("Confirmation with id '{}' has been deleted", id);
    }
}
