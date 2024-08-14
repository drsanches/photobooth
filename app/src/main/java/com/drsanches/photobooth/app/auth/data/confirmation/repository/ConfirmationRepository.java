package com.drsanches.photobooth.app.auth.data.confirmation.repository;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface ConfirmationRepository extends CrudRepository<Confirmation, String> {

    @Override
    @NonNull
    <S extends Confirmation> S save(@NonNull S entity);

    Optional<Confirmation> findByCode(String code);

    boolean existsByNewUsername(String newUsername);

    boolean existsByNewEmail(String newEmail);

    List<Confirmation> findByExpiresLessThan(GregorianCalendar expires);

    @Override
    void deleteById(@NonNull String s);

    @Override
    void deleteAll(@NonNull Iterable<? extends Confirmation> entities);
}
