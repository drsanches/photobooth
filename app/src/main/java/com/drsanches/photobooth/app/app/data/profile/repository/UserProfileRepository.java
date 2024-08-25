package com.drsanches.photobooth.app.app.data.profile.repository;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    Optional<UserProfile> findByIdAndEnabled(String userId, boolean enabled);

    List<UserProfile> findAllByIdInOrderByUsername(Collection<String> userIds);

    List<UserProfile> findByUsernameContainingAndEnabled(String username, boolean enabled, Pageable pageable);

    List<UserProfile> findAllByIdInAndEnabled(Collection<String> userIds, boolean enabled);

    @Modifying
    @Transactional
    @Query("UPDATE UserProfile profile SET profile.username = ?2 WHERE profile.id = ?1")
    void updateUsername(String userId, String username);

    @Modifying
    @Transactional
    @Query("UPDATE UserProfile profile SET profile.name = ?2, profile.status = ?3 WHERE profile.id = ?1")
    void updateNameAndStatus(String userId, String name, String status);

    @Modifying
    @Transactional
    @Query("UPDATE UserProfile profile SET profile.imageId = ?2 WHERE profile.id = ?1")
    void updateImageId(String userId, String imageId);
}
