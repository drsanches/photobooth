package com.drsanches.photobooth.app.auth.data.confirmation;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.confirmation.repository.ConfirmationRepository;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ConfirmationDomainService {

    @Autowired
    private ConfirmationRepository confirmationRepository;

    @Autowired
    private ConfirmationCodeGenerator confirmationCodeGenerator;

    public Confirmation create(
            Operation operation,
            Instant expires,
            @Nullable String userId,
            @Nullable String newUsername,
            @Nullable String newEmail,
            @Nullable String data
    ) {
        var savedConfirmation = confirmationRepository.save(Confirmation.builder()
                .id(UUID.randomUUID().toString())
                .code(confirmationCodeGenerator.generate())
                .userId(userId)
                .newUsername(newUsername)
                .newEmail(newEmail)
                .operation(operation)
                .data(data)
                .expires(expires)
                .build());
        log.info("New confirmation saved: {}", savedConfirmation);
        return savedConfirmation;
    }

    public boolean existsByNewUsername(String newUsername) {
        return confirmationRepository.existsByNewUsername(newUsername);
    }

    public boolean existsByNewEmail(String newEmail) {
        return confirmationRepository.existsByNewEmail(newEmail);
    }

    public Optional<Confirmation> findByCode(String code) {
        return confirmationRepository.findByCode(code);
    }

    public List<Confirmation> findAllExpired() {
        return confirmationRepository.findByExpiresLessThan(Instant.now());
    }

    public void delete(String id) {
        confirmationRepository.deleteById(id);
        log.info("Confirmation deleted. Id: {}", id);
    }

    public void deleteAll(List<Confirmation> confirmations) {
        confirmationRepository.deleteAll(confirmations);
        log.info("Confirmations deleted: {}", confirmations);
    }
}
