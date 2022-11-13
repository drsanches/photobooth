package ru.drsanches.photobooth.app.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.app.data.profile.dto.request.ChangeUserProfileDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.RelationshipDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.FriendsDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        return userInfoMapper.convert(userProfile, RelationshipDTO.CURRENT);
    }

    @Deprecated
    public UserInfoDTO searchProfile(String username) {
        String currentUserId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledByUsername(username.toLowerCase());
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        return userInfoMapper.convert(userProfile, incomingIds, outgoingIds);
    }

    public List<UserInfoDTO> searchProfile(String username, Integer page, Integer size) {
        String currentUserId = tokenSupplier.get().getUserId();
        List<UserProfile> userProfile = userProfileDomainService.findEnabledByUsername(username.toLowerCase(), page, size);
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        return userProfile.stream()
                .map(x -> userInfoMapper.convert(x, incomingIds, outgoingIds))
                .collect(Collectors.toList());
    }

    public UserInfoDTO getProfile(String userId) {
        String currentUserId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        return userInfoMapper.convert(userProfile, incomingIds, outgoingIds);
    }

    public void changeCurrentProfile(@Valid ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setName(changeUserProfileDTO.getName());
        userProfile.setStatus(changeUserProfileDTO.getStatus());
        userProfileDomainService.save(userProfile);
        log.info("User with id '{}' updated his profile", userId);
    }
}
