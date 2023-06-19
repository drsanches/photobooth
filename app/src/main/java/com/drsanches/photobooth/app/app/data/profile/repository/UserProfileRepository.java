package com.drsanches.photobooth.app.app.data.profile.repository;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface UserProfileRepository extends CrudRepository<UserProfile, String> {

    @Override
    @NonNull
    <S extends UserProfile> S save(@NonNull S entity);

    @Override
    @NonNull
    Optional<UserProfile> findById(@NonNull String s);

    @Override
    @NonNull
    Iterable<UserProfile> findAllById(@NonNull Iterable<String> strings);

    List<UserProfile> findAllByIdInOrderByUsername(Collection<String> userIds);

    List<UserProfile> findByUsernameContainingAndEnabled(String username, boolean enabled, Pageable pageable);

    @Override
    boolean existsById(@NonNull String s);
}
