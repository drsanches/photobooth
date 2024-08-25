package com.drsanches.photobooth.app.auth.data.userauth.repository;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@MonitorTime
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    @Override
    @NonNull
    <S extends UserAuth> S save(@NonNull S entity);

    Optional<UserAuth> findByIdAndEnabled(String userId, boolean enabled);

    Optional<UserAuth> findByUsernameAndEnabled(String username, boolean enabled);

    Optional<UserAuth> findByEmailAndEnabled(String email, boolean enabled);

    Optional<UserAuth> findByGoogleAuthAndEnabled(String googleAuth, boolean enabled);

    @Modifying
    @Transactional
    @Query("UPDATE UserAuth user SET user.username = ?2 WHERE user.id = ?1")
    void updateUsername(String userId, String username);

    @Modifying
    @Transactional
    @Query("UPDATE UserAuth user SET user.password = ?2, user.salt = ?3 WHERE user.id = ?1")
    void updatePassword(String userId, String password, String salt);

    @Modifying
    @Transactional
    @Query("UPDATE UserAuth user SET user.email = ?2 WHERE user.id = ?1")
    void updateEmail(String userId, String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserAuth user SET user.googleAuth = ?2 WHERE user.id = ?1")
    void updateGoogleAuth(String userId, String googleAuth);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByGoogleAuth(String googleAuth);
}
