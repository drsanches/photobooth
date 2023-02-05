package com.drsanches.photobooth.app.auth.data.confirmation.repository;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationRepository extends CrudRepository<Confirmation, String> {

    Optional<Confirmation> findByCode(String code);

    List<Confirmation> findByExpiresAtLessThan(GregorianCalendar expiresAt);
}
