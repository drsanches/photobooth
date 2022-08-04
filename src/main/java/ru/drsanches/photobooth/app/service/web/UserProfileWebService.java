package ru.drsanches.photobooth.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;

@Service
public class UserProfileWebService {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileWebService.class);

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDTO getCurrentProfile() {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile);
    }

    public UserInfoDTO searchProfile(String username) {
        UserProfile userProfile = userProfileDomainService.getEnabledByUsername(username);
        return userInfoMapper.convert(userProfile);
    }

    public UserInfoDTO getProfile(String userId) {
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile);
    }

    public void changeCurrentProfile(ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setFirstName(changeUserProfileDTO.getFirstName());
        userProfile.setLastName(changeUserProfileDTO.getLastName());
        userProfileDomainService.save(userProfile);
        LOG.info("User with id '{}' updated his profile", userId);
    }
}