package ru.drsanches.photobooth.app.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.app.data.profile.dto.request.ChangeUserProfileDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.Relationship;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.FriendsDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
@Validated
public class UserProfileWebService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDTO getCurrentProfile() {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile, Relationship.CURRENT);
    }

    public UserInfoDTO searchProfile(String username) {
        UserProfile userProfile = userProfileDomainService.getEnabledByUsername(username.toLowerCase());
        return userInfoMapper.convert(userProfile, getRelationship(userProfile.getId()));
    }

    public UserInfoDTO getProfile(String userId) {
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile, getRelationship(userProfile.getId()));
    }

    public void changeCurrentProfile(@Valid ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setName(changeUserProfileDTO.getName());
        userProfile.setStatus(changeUserProfileDTO.getStatus());
        userProfileDomainService.save(userProfile);
        log.info("User with id '{}' updated his profile", userId);
    }

    private Relationship getRelationship(String userId) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId.equals(userId)) {
            return Relationship.CURRENT;
        }
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        if (incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return Relationship.FRIEND;
        } else if (incomingIds.contains(userId) && !outgoingIds.contains(userId)) {
            return Relationship.INCOMING_FRIEND_REQUEST;
        } else if (!incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return Relationship.OUTGOING_FRIEND_REQUEST;
        }
        return Relationship.STRANGER;
    }
}
