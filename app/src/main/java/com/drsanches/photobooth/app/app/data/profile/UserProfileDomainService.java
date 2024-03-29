package com.drsanches.photobooth.app.app.data.profile;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserProfileDomainService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PaginationService<UserProfile> paginationService; //TODO: Exclude?

    public void updateProfileData(String userId, @Nullable String name, @Nullable String status) {
        var userProfile = getEnabledById(userId);
        userProfile.setName(name);
        userProfile.setStatus(status);
        userProfileRepository.save(userProfile);
        log.debug("UserProfile data updated: {}", userProfile);
    }

    public void updateImageId(String userId, @Nullable String imageId) {
        var userProfile = getEnabledById(userId);
        userProfile.setImageId(imageId);
        userProfileRepository.save(userProfile);
        log.debug("UserProfile imageId updated: {}", userProfile);
    }

    public UserProfile getEnabledById(String userId) {
        return userProfileRepository.findByIdAndEnabled(userId, true)
                .orElseThrow(() -> new NoUserIdException(userId));
    }

    //TODO: Sort?
    public List<UserProfile> findEnabledByUsername(String username, Integer page, Integer size) {
        var pageable = paginationService.pageable(page, size);
        return userProfileRepository.findByUsernameContainingAndEnabled(username, true, pageable);
    }

    public boolean enabledExistsById(String userId) {
        return userProfileRepository.existsByIdAndEnabled(userId, true);
    }

    public boolean anyExistsById(String userId) {
        return userProfileRepository.existsById(userId);
    }

    public List<UserProfile> getEnabledByIds(Collection<String> userIds) {
        return userProfileRepository.findAllByIdInAndEnabled(userIds, true);
    }

    public List<UserProfile> getAllByIdsOrderByUsername(Collection<String> userIds) {
        return userProfileRepository.findAllByIdInOrderByUsername(userIds);
    }
}
