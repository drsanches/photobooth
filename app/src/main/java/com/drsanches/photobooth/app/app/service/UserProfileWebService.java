package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.config.UserInfo;
import com.drsanches.photobooth.app.app.dto.profile.request.ChangeUserProfileDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
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
    private UserInfo userInfo;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDto getCurrentProfile() {
        var userId = userInfo.getUserId();
        var userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convertCurrent(
                userProfile,
                friendsDomainService.getIncomingRequestsCount(userId),
                friendsDomainService.getOutgoingRequestsCount(userId),
                friendsDomainService.getFriendsCount(userId)
        );
    }

    public List<UserInfoDto> searchProfile(String username, Integer page, Integer size) {
        var currentUserId = userInfo.getUserId();
        var userProfile = userProfileDomainService.findEnabledByUsername(username.toLowerCase(), page, size);
        var incomingIds = friendsDomainService.getIncomingRequestAndFriendIds(currentUserId);
        var outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIds(currentUserId);
        return userProfile.stream()
                .filter(x -> !x.getId().equals(currentUserId))
                .map(x -> userInfoMapper.convert(x, incomingIds, outgoingIds))
                .collect(Collectors.toList());
    }

    public UserInfoDto getProfile(String userId) {
        var currentUserId = userInfo.getUserId();
        var userProfile = userProfileDomainService.getEnabledById(userId);
        var incomingIds = friendsDomainService.getIncomingRequestAndFriendIds(currentUserId);
        var outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIds(currentUserId);
        return userInfoMapper.convert(userProfile, incomingIds, outgoingIds);
    }

    public void changeCurrentProfile(@Valid ChangeUserProfileDto changeUserProfileDto) {
        var userId = userInfo.getUserId();
        userProfileDomainService.updateProfileData(
                userId,
                changeUserProfileDto.getName(),
                changeUserProfileDto.getStatus()
        );
        log.info("User updated his profile. UserId: {}", userId);
    }
}
