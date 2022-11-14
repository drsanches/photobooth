package ru.drsanches.photobooth.auth.data.confirmation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.photobooth.auth.data.confirmation.model.Confirmation;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends CrudRepository<Confirmation, String> {

    Optional<Confirmation> findByCode(String code);
}
