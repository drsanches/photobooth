package ru.drsanches.photobooth.app.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadAvatarDTO;
import ru.drsanches.photobooth.app.data.image.dto.UploadPhotoDTO;
import ru.drsanches.photobooth.app.data.image.mapper.ImageInfoMapper;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.image.model.ImagePermission;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.app.service.domain.ImagePermissionDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
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

@Slf4j
@Service
@Validated
public class ImageWebService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    @Autowired
    private ImagePermissionDomainService imagePermissionDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private ImageInfoMapper imageInfoMapper;

    public void uploadAvatar(@Valid UploadAvatarDTO uploadAvatarDTO) {
        String userId = tokenSupplier.get().getUserId();
        String imageId = UUID.randomUUID().toString();
        byte[] file = Base64.getDecoder().decode(uploadAvatarDTO.getFile());
        GregorianCalendar createdTime = new GregorianCalendar();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        imageDomainService.saveImage(new Image(imageId, file, createdTime, userId));
        userProfile.setImageId(imageId);
        userProfileDomainService.save(userProfile);
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
        imageDomainService.saveImage(new Image(imageId, file, createdTime, currentUserId));
        uploadPhotoDTO.getUserIds().add(currentUserId);
        List<ImagePermission> imagePermissions = new ArrayList<>(uploadPhotoDTO.getUserIds().size());
        uploadPhotoDTO.getUserIds().forEach(userId -> imagePermissions.add(new ImagePermission(imageId, userId)));
        imagePermissionDomainService.savePermissions(imagePermissions);
        log.info("Photo with id '{}' uploaded for users: {}", imageId, uploadPhotoDTO.getUserIds());
    }

    public List<ImageInfoDTO> getAllInfo() {
        String currentUserId = tokenSupplier.get().getUserId();
        Set<String> imageIds = imagePermissionDomainService.getImageIds(currentUserId);
        List<Image> images = imageDomainService.getImages(imageIds);
        return images.stream()
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
}
