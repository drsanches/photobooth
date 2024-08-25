package com.drsanches.photobooth.app.app.data.profile;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserProfileDomainService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PaginationService<UserProfile> paginationService; //TODO: Exclude?

    public UserProfile create(String userId, String username) {
        var userProfile = UserProfile.builder()
                .id(userId)
                .username(username)
                .enabled(true)
                .build();
        userProfileRepository.save(userProfile);
        log.info("New UserProfile saved: {}", userProfile);
        return userProfile;
    }

    public void updateUsername(String userId, String username) {
        userProfileRepository.updateUsername(userId, username);
        log.info("UserProfile username updated. UserId: {}, username: {}", userId, username);
    }

    public void updateProfileData(String userId, @Nullable String name, @Nullable String status) {
        userProfileRepository.updateNameAndStatus(userId, name, status);
        log.info("UserProfile data updated. UserId: {}, name: {}, status: {}", userId, name, status);
    }

    public void updateImageId(String userId, @Nullable String imageId) {
        userProfileRepository.updateImageId(userId, imageId);
        log.info("UserProfile imageId updated. UserId: {}, imageId: {}", userId, imageId);
    }

    public Optional<UserProfile> findById(String userId) {
        return userProfileRepository.findById(userId);
    }

    public Optional<UserProfile> findEnabledById(String userId) {
        return userProfileRepository.findByIdAndEnabled(userId, true);
    }

    //TODO: Sort?
    public List<UserProfile> findEnabledByUsername(String username, Integer page, Integer size) {
        var pageable = paginationService.pageable(page, size);
        return userProfileRepository.findByUsernameContainingAndEnabled(username, true, pageable);
    }

    public List<UserProfile> findAllEnabledByIds(Collection<String> userIds) {
        return userProfileRepository.findAllByIdInAndEnabled(userIds, true);
    }

    public List<UserProfile> findAllByIdsOrderByUsername(Collection<String> userIds) {
        return userProfileRepository.findAllByIdInOrderByUsername(userIds);
    }

    public void disableUser(String userId) {
        var userProfile = findEnabledById(userId).orElseThrow();
        var savedUserProfile = userProfileRepository.save(userProfile.toBuilder()
                .username(UUID.randomUUID() + "_" + userProfile.getUsername())
                .enabled(false)
                .build());
        log.info("UserProfile disabled: {}", savedUserProfile);
    }
}
