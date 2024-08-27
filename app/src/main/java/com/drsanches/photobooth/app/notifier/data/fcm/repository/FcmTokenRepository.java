package com.drsanches.photobooth.app.notifier.data.fcm.repository;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface FcmTokenRepository extends CrudRepository<FcmToken, String> {

    @Override
    @NonNull
    <S extends FcmToken> S save(@NonNull S entity);

    List<FcmToken> findByUserId(String userId);

    List<FcmToken> findByTokenIn(List<String> tokens);

    List<FcmToken> findByExpiresLessThan(Instant expires);

    Optional<FcmToken> findByToken(String token);
}
