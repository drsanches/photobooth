package com.drsanches.photobooth.app.app.service.domain;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.service.utils.PaginationService;
import com.drsanches.photobooth.app.app.data.profile.repository.UserProfileRepository;
import com.drsanches.photobooth.app.common.exception.application.NoUserIdException;
import com.drsanches.photobooth.app.common.exception.application.NoUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileDomainService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PaginationService<UserProfile> paginationService;

    public void save(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
        log.debug("UserProfile updated: {}", userProfile);
    }

    public UserProfile getEnabledById(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            throw new NoUserIdException(userId);
        }
        return userProfile.get();
    }

    @Deprecated
    public UserProfile getEnabledByUsername(String username) {
        Optional<UserProfile> userProfile = userProfileRepository.findByUsername(username);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            throw new NoUsernameException(username);
        }
        return userProfile.get();
    }

    //TODO: Sort?
    public List<UserProfile> findEnabledByUsername(String username, Integer page, Integer size) {
        Pageable pageable = paginationService.pageable(page, size);
        return userProfileRepository.findByUsernameContainingAndEnabled(username, true, pageable);
    }

    public boolean enabledExistsById(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        return userProfile.isPresent() && userProfile.get().isEnabled();
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
