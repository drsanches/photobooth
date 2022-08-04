package ru.drsanches.photobooth.app.data.profile.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {

    Optional<UserProfile> findByUsername(String username);
}