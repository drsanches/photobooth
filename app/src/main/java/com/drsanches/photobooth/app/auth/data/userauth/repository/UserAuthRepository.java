package com.drsanches.photobooth.app.auth.data.userauth.repository;

import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    Optional<UserAuth> findByUsername(String username);

    Optional<UserAuth> findByGoogleAuth(String googleAuth);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
