package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.mapper.ImageInfoMapper;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.dto.image.request.UploadAvatarDto;
import com.drsanches.photobooth.app.app.dto.image.request.UploadPhotoDto;
import com.drsanches.photobooth.app.app.dto.image.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.data.permission.ImagePermissionDomainService;
import com.drsanches.photobooth.app.common.token.UserInfo;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.notifier.service.notifier.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class ImageWebService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    @Autowired
    private ImagePermissionDomainService imagePermissionDomainService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private PaginationService<Image> paginationService;

    @Autowired
    private ImageInfoMapper imageInfoMapper;

    public void uploadAvatar(@Valid UploadAvatarDto uploadAvatarDto) {
        var userId = userInfo.getUserId();
        var image = Base64.getDecoder().decode(uploadAvatarDto.getFile());
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            var imageId = imageDomainService.saveImage(image, userId).getId();
            userProfileDomainService.updateImageId(userId, imageId);
            log.info("User updated profile image. UserId: {}, newImageId: {}", userId, imageId);
        });
    }

    public ImageInfoDto getImageInfo(String imageId) {
        return imageInfoMapper.convert(imageDomainService.getImage(imageId));
    }

    public byte[] getImage(String imageId) {
        return imageDomainService.getImage(imageId).getData();
    }

    public byte[] getThumbnail(String imageId) {
        return imageDomainService.getImage(imageId).getThumbnailData();
    }

    public void uploadPhoto(@Valid UploadPhotoDto uploadPhotoDto) {
        var currentUserId = userInfo.getUserId();
        var image = Base64.getDecoder().decode(uploadPhotoDto.getFile());
        var allowedUsers = CollectionUtils.isEmpty(uploadPhotoDto.getUserIds()) ?
                getEnabledFriends(currentUserId) :
                uploadPhotoDto.getUserIds();
        var imageRecipients = String.join(",", allowedUsers);
        allowedUsers.add(currentUserId);
        var imageId = new TransactionTemplate(transactionManager).execute(status -> {
            var savedImageId = imageDomainService.saveImage(image, currentUserId).getId();
            imagePermissionDomainService.savePermissions(savedImageId, allowedUsers);
            log.info("User uploaded new image. UserId: {}, imageId: {}, allowedUserIds: {}",
                    currentUserId, savedImageId, allowedUsers);
            return savedImageId;
        });
        notificationService.notify(Action.IMAGE_SENT, Map.of(
                "fromUser", currentUserId,
                "toUsers", imageRecipients,
                "imageId", imageId
        ));
    }

    public List<ImageInfoDto> getAllInfo(Integer page, Integer size) {
        var currentUserId = userInfo.getUserId();
        var imageIds = imagePermissionDomainService.getImageIds(currentUserId);
        var images = imageDomainService.getImages(imageIds).stream();
        return paginationService.pagination(images, page, size)
                .map(imageInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public void deleteAvatar() {
        var userId = userInfo.getUserId();
        userProfileDomainService.updateImageId(userId, null);
        log.info("User deleted his profile image. UserId: {}", userId);
    }

    private List<String> getEnabledFriends(String currentUserId) {
        var friendIds = friendsDomainService.getFriendsIds(currentUserId);
        return userProfileDomainService.getEnabledByIds(friendIds).stream()
                .map(UserProfile::getId)
                .collect(Collectors.toList());
    }
}
