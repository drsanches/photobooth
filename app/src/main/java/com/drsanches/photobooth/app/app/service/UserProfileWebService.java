package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.app.dto.profile.request.UploadProfilePhotoDto;
import com.drsanches.photobooth.app.app.dto.profile.request.UpdateUserProfileDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import java.util.Base64;
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
    private ImageDomainService imageDomainService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private AuthInfo authInfo;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDto getCurrentProfile() {
        var userId = authInfo.getUserId();
        var userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convertCurrent(
                userProfile,
                friendsDomainService.getIncomingRequestsCount(userId),
                friendsDomainService.getOutgoingRequestsCount(userId),
                friendsDomainService.getFriendsCount(userId)
        );
    }

    public void updateCurrentProfile(@Valid UpdateUserProfileDto updateUserProfileDto) {
        var userId = authInfo.getUserId();
        userProfileDomainService.updateProfileData(
                userId,
                updateUserProfileDto.getName(),
                updateUserProfileDto.getStatus()
        );
        log.info("User updated his profile. UserId: {}", userId);
    }

    public UserInfoDto getProfile(String userId) {
        var currentUserId = authInfo.getUserId();
        var userProfile = userProfileDomainService.getEnabledById(userId);
        var incomingIds = friendsDomainService.getIncomingRequestAndFriendIds(currentUserId);
        var outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIds(currentUserId);
        return userInfoMapper.convert(userProfile, incomingIds, outgoingIds);
    }

    public List<UserInfoDto> searchProfile(String username, Integer page, Integer size) {
        var currentUserId = authInfo.getUserId();
        var userProfile = userProfileDomainService.findEnabledByUsername(username.toLowerCase(), page, size);
        var incomingIds = friendsDomainService.getIncomingRequestAndFriendIds(currentUserId);
        var outgoingIds = friendsDomainService.getOutgoingRequestAndFriendIds(currentUserId);
        return userProfile.stream()
                .filter(x -> !x.getId().equals(currentUserId))
                .map(x -> userInfoMapper.convert(x, incomingIds, outgoingIds))
                .collect(Collectors.toList());
    }

    public void uploadProfilePhoto(@Valid UploadProfilePhotoDto uploadProfilePhotoDto) {
        var userId = authInfo.getUserId();
        var image = Base64.getDecoder().decode(uploadProfilePhotoDto.getImageData());
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            var imageId = imageDomainService.saveImage(image, userId).getId();
            userProfileDomainService.updateImageId(userId, imageId);
            log.info("User updated profile image. UserId: {}, newImageId: {}", userId, imageId);
        });
    }

    public void deleteProfilePhoto() {
        var userId = authInfo.getUserId();
        userProfileDomainService.updateImageId(userId, null);
        log.info("User deleted his profile image. UserId: {}", userId);
    }
}
