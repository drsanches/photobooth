package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.exception.ImageNotFoundException;
import com.drsanches.photobooth.app.app.mapper.ImageInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.utils.PagingService;
import com.drsanches.photobooth.app.app.dto.image.request.UploadPhotoDto;
import com.drsanches.photobooth.app.app.dto.image.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.data.permission.ImagePermissionDomainService;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationParams;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationService;
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
    private AuthInfo authInfo;

    @Autowired
    private PagingService pagingService;

    @Autowired
    private ImageInfoMapper imageInfoMapper;

    public ImageInfoDto getImageInfo(String imageId) {
        var image = imageDomainService.findImage(imageId)
                .orElseThrow(ImageNotFoundException::new);
        return imageInfoMapper.convert(image);
    }

    public byte[] getImage(String imageId) {
        return imageDomainService.findImage(imageId)
                .orElseThrow(ImageNotFoundException::new)
                .getData();
    }

    public byte[] getThumbnail(String imageId) {
        return imageDomainService.findImage(imageId)
                .orElseThrow(ImageNotFoundException::new)
                .getThumbnailData();
    }

    public void uploadPhoto(@Valid UploadPhotoDto uploadPhotoDto) {
        var currentUserId = authInfo.getUserId();
        var image = Base64.getDecoder().decode(uploadPhotoDto.getImageData());
        var allowedUsers = CollectionUtils.isEmpty(uploadPhotoDto.getUserIds()) ?
                getEnabledFriends(currentUserId) :
                uploadPhotoDto.getUserIds();
        var imageRecipients = List.copyOf(allowedUsers);
        allowedUsers.add(currentUserId);
        var imageId = new TransactionTemplate(transactionManager).execute(status -> {
            var savedImageId = imageDomainService.saveImage(
                    image,
                    currentUserId,
                    uploadPhotoDto.getGeo() == null ? null : uploadPhotoDto.getGeo().getLat(),
                    uploadPhotoDto.getGeo() == null ? null : uploadPhotoDto.getGeo().getLng()
            ).getId();
            imagePermissionDomainService.saveAll(savedImageId, allowedUsers);
            log.info("User uploaded new image. UserId: {}, imageId: {}, allowedUserIds: {}",
                    currentUserId, savedImageId, allowedUsers);
            return savedImageId;
        });
        notificationService.notify(Action.IMAGE_SENT, NotificationParams.builder()
                .fromUser(currentUserId)
                .toUsers(imageRecipients)
                .imageId(imageId)
                .build());
    }

    public List<ImageInfoDto> getAllInfo(Integer page, Integer size) {
        var currentUserId = authInfo.getUserId();
        var imageIds = imagePermissionDomainService.findAllByImageId(currentUserId);
        var pageable = pagingService.pageable(page, size);
        return imageDomainService.findAllImagesByIds(imageIds, pageable).stream()
                .map(imageInfoMapper::convert)
                .toList();
    }

    private List<String> getEnabledFriends(String currentUserId) {
        var friendIds = friendsDomainService.findOnlyFriendIds(currentUserId);
        return userProfileDomainService.findAllEnabledByIds(friendIds).stream()
                .map(UserProfile::getId)
                .collect(Collectors.toList());
    }
}
