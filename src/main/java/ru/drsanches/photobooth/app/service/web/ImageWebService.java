package ru.drsanches.photobooth.app.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.app.data.image.dto.response.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.dto.request.UploadAvatarDTO;
import ru.drsanches.photobooth.app.data.image.dto.request.UploadPhotoDTO;
import ru.drsanches.photobooth.app.data.image.mapper.ImageInfoMapper;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.image.model.ImagePermission;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.FriendsDomainService;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.app.service.domain.ImagePermissionDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.app.service.utils.PaginationService;
import ru.drsanches.photobooth.common.token.TokenSupplier;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Base64;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private TokenSupplier tokenSupplier;

    @Autowired
    private PaginationService<Image> paginationService;

    @Autowired
    private ImageInfoMapper imageInfoMapper;

    public void uploadAvatar(@Valid UploadAvatarDTO uploadAvatarDTO) {
        String userId = tokenSupplier.get().getUserId();
        String imageId = UUID.randomUUID().toString();
        byte[] file = Base64.getDecoder().decode(uploadAvatarDTO.getFile());
        GregorianCalendar createdTime = new GregorianCalendar();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setImageId(imageId);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            imageDomainService.saveImage(new Image(imageId, file, createdTime, userId));
            userProfileDomainService.save(userProfile);
        });
        log.info("User with id '{}' updated his profile image, new image id is '{}'", userId, imageId);
    }

    public ImageInfoDTO getImageInfo(String imageId) {
        return imageInfoMapper.convert(imageDomainService.getImage(imageId));
    }

    public byte[] getImage(String imageId) {
        return imageDomainService.getImage(imageId).getData();
    }

    public void uploadPhoto(@Valid UploadPhotoDTO uploadPhotoDTO) {
        String currentUserId = tokenSupplier.get().getUserId();
        String imageId = UUID.randomUUID().toString();
        byte[] file = Base64.getDecoder().decode(uploadPhotoDTO.getFile());
        GregorianCalendar createdTime = new GregorianCalendar();
        List<String> allowedUsers = CollectionUtils.isEmpty(uploadPhotoDTO.getUserIds()) ?
                getEnabledFriends(currentUserId) : uploadPhotoDTO.getUserIds();
        allowedUsers.add(currentUserId);
        List<ImagePermission> imagePermissions = new ArrayList<>(allowedUsers.size());
        allowedUsers.forEach(userId -> imagePermissions.add(new ImagePermission(imageId, userId)));
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            imageDomainService.saveImage(new Image(imageId, file, createdTime, currentUserId));
            imagePermissionDomainService.savePermissions(imagePermissions);
        });
        log.info("Photo with id '{}' uploaded for users: {}", imageId, uploadPhotoDTO.getUserIds());
    }

    public List<ImageInfoDTO> getAllInfo(Integer page, Integer size) {
        String currentUserId = tokenSupplier.get().getUserId();
        Set<String> imageIds = imagePermissionDomainService.getImageIds(currentUserId);
        Stream<Image> images = imageDomainService.getImages(imageIds).stream();
        return paginationService.pagination(images, page, size)
                .map(imageInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public ImageInfoDTO getLastImageInfo() {
        String currentUserId = tokenSupplier.get().getUserId();
        Set<String> imageIds = imagePermissionDomainService.getImageIds(currentUserId);
        Optional<Image> image = imageDomainService.getLastImage(imageIds, currentUserId);
        return imageInfoMapper.convert(image.orElse(imageDomainService.getImage(ImageInfoMapper.NO_PHOTO_IMAGE_ID)));
    }

    public void deleteAvatar() {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setImageId(null);
        userProfileDomainService.save(userProfile);
        log.info("User with id '{}' deleted his profile image", userId);
    }

    private List<String> getEnabledFriends(String currentUserId) {
        List<String> friendIds = friendsDomainService.getFriendsIdList(currentUserId);
        return userProfileDomainService.getEnabledByIds(friendIds).stream()
                .map(UserProfile::getId)
                .collect(Collectors.toList());
    }
}
