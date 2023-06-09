package com.drsanches.photobooth.app.app.data.profile;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class UserProfileDomainService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PaginationService<UserProfile> paginationService; //TODO: Exclude?

    public void save(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
        log.debug("UserProfile updated: {}", userProfile);
    }

    public UserProfile getEnabledById(String userId) {
        return userProfileRepository.findById(userId)
                .filter(UserProfile::isEnabled)
                .orElseThrow(() -> {
                    throw new NoUserIdException(userId);
                });
    }

    @Deprecated
    public UserProfile getEnabledByUsername(String username) {
        return userProfileRepository.findByUsername(username)
                .filter(UserProfile::isEnabled)
                .orElseThrow(() -> {
                    throw new NoUsernameException(username);
                });
    }

    //TODO: Sort?
    public List<UserProfile> findEnabledByUsername(String username, Integer page, Integer size) {
        Pageable pageable = paginationService.pageable(page, size);
        return userProfileRepository.findByUsernameContainingAndEnabled(username, true, pageable);
    }

    public boolean enabledExistsById(String userId) {
        return userProfileRepository.findById(userId)
                .filter(UserProfile::isEnabled)
                .isPresent();
    }

    public boolean anyExistsById(String userId) {
        return userProfileRepository.existsById(userId);
    }

    public List<UserProfile> getEnabledByIds(Collection<String> userIds) {
        List<UserProfile> profiles = new LinkedList<>();
        userProfileRepository.findAllById(userIds).forEach(profile -> {
            if (profile.isEnabled()) {
                profiles.add(profile);
            }
        });
        return profiles;
    }

    public List<UserProfile> getAllByIdsOrderByUsername(Collection<String> userIds) {
        return userProfileRepository.findAllByIdInOrderByUsername(userIds);
    }
}
