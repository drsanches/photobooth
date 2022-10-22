package ru.drsanches.photobooth.auth.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.photobooth.auth.data.model.UserAuth;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends CrudRepository<UserAuth, String> {

    Optional<UserAuth> findByUsername(String username);

    Optional<UserAuth> findByGoogleAuth(String googleAuth);
}
