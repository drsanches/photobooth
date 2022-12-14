package ru.drsanches.photobooth.app.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.data.profile.repository.UserProfileRepository;
import ru.drsanches.photobooth.app.service.utils.PaginationService;
import ru.drsanches.photobooth.common.exception.application.NoUserIdException;
import ru.drsanches.photobooth.common.exception.application.NoUsernameException;

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
        log.info("UserProfile updated: {}", userProfile);
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
