package ru.drsanches.photobooth.auth.service.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.data.confirmation.model.Confirmation;
import ru.drsanches.photobooth.auth.data.confirmation.repository.ConfirmationRepository;
import ru.drsanches.photobooth.exception.auth.WrongConfirmCodeException;

import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConfirmationDomainService {

    private static final int CALENDAR_FIELD = GregorianCalendar.MINUTE;

    private static final int CALENDAR_VALUE = 5;

    @Autowired
    private ConfirmationRepository confirmationRepository;

    public String save(String data) {
        Confirmation confirmation = new Confirmation();
        confirmation.setCode(UUID.randomUUID().toString());
        confirmation.setData(data);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        confirmation.setExpiresAt(expiresAt);
        confirmationRepository.save(confirmation);
        return confirmation.getCode();
    }

    public Confirmation getNotExpired(String code) {
        Optional<Confirmation> optionalConfirmation = confirmationRepository.findByCode(code);
        if (optionalConfirmation.isEmpty()) {
            throw new WrongConfirmCodeException("Wrong confirmation code");
        }
        Confirmation confirmation = optionalConfirmation.get();
        if (confirmation.getExpiresAt().before(new GregorianCalendar())) {
            confirmationRepository.delete(confirmation);
            throw new WrongConfirmCodeException("Confirmation code has been expired");
        }
        return confirmation;
    }

    public void delete(String code) {
        confirmationRepository.deleteById(code);
    }
}
