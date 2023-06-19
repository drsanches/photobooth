package com.drsanches.photobooth.app.common.token.data.repository;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface TokenRepository extends CrudRepository<Token, String> {

    @Override
    @NonNull
    <S extends Token> S save(@NonNull S entity);

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

    List<Token> findByUserId(String userId);

    List<Token> findByExpiresLessThanAndRefreshExpiresLessThan(GregorianCalendar expires, GregorianCalendar refreshExpires);

    @Override
    void deleteById(@NonNull String s);

    @Override
    void deleteAll(@NonNull Iterable<? extends Token> entities);
}
