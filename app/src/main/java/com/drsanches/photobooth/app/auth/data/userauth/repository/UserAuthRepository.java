package com.drsanches.photobooth.app.auth.data.userauth.repository;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@MonitorTime
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    @Override
    @NonNull
    <S extends UserAuth> S save(@NonNull S entity);

    @Override
    @NonNull
    Optional<UserAuth> findById(@NonNull String s);

    Optional<UserAuth> findByUsername(String username);

    Optional<UserAuth> findByGoogleAuth(String googleAuth);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
