package ru.drsanches.photobooth.app.data.profile.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {

    List<UserProfile> findAllByIdInOrderByUsername(Collection<String> userIds);

    @Deprecated
    Optional<UserProfile> findByUsername(String username);

    List<UserProfile> findByUsernameContainingAndEnabled(String username, boolean enabled, Pageable pageable);
}
