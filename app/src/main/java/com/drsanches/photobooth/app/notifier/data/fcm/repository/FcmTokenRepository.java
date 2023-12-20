package com.drsanches.photobooth.app.notifier.data.fcm.repository;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.notifier.data.fcm.model.FcmToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.GregorianCalendar;
import java.util.List;

@Repository
@MonitorTime
public interface FcmTokenRepository extends CrudRepository<FcmToken, String> {

    @Override
    @NonNull
    <S extends FcmToken> S save(@NonNull S entity);

    List<FcmToken> findByUserId(String userId);

    List<FcmToken> findByTokenIn(List<String> tokens);

    List<FcmToken> findByExpiresLessThan(GregorianCalendar expires);

    boolean existsByToken(String token);
}
