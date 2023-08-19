package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.dto.profile.request.ChangeUserProfileDto;
import com.drsanches.photobooth.app.app.dto.profile.response.RelationshipDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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

    public UserInfoDto getCurrentProfile() {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile, RelationshipDto.CURRENT);
    }

    public List<UserInfoDto> searchProfile(String username, Integer page, Integer size) {
        String currentUserId = tokenSupplier.get().getUserId();
        List<UserProfile> userProfile = userProfileDomainService.findEnabledByUsername(username.toLowerCase(), page, size);
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        return userProfile.stream()
                .map(x -> userInfoMapper.convert(x, incomingIds, outgoingIds))
                .collect(Collectors.toList());
    }

    public UserInfoDto getProfile(String userId) {
        String currentUserId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        List<String> incomingIds = friendsDomainService.getIncomingRequestAndFriendIdList(currentUserId);
        List<String> outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIdList(currentUserId);
        return userInfoMapper.convert(userProfile, incomingIds, outgoingIds);
    }

    public void changeCurrentProfile(@Valid ChangeUserProfileDto changeUserProfileDto) {
        String userId = tokenSupplier.get().getUserId();
        userProfileDomainService.updateProfileData(
                userId,
                changeUserProfileDto.getName(),
                changeUserProfileDto.getStatus()
        );
        log.info("User updated his profile. UserId: {}", userId);
    }
}
