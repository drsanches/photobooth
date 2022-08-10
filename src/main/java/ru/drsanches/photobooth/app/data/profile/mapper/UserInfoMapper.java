package ru.drsanches.photobooth.app.data.profile.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.controller.ImageController;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;

@Component
public class UserInfoMapper {

    private static final String DEFAULT_ID = "default";

    public UserInfoDTO convert(UserProfile userProfile) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userProfile.getId());
        if (userProfile.isEnabled()) {
            userInfoDTO.setUsername(userProfile.getUsername());
            userInfoDTO.setName(userProfile.getName());
            userInfoDTO.setStatus(userProfile.getStatus());
            userInfoDTO.setImagePath(userProfile.getImageId() == null ?
                    ImageController.IMAGE_PATH_PREFIX + DEFAULT_ID :
                    ImageController.IMAGE_PATH_PREFIX + userProfile.getImageId());
        }
        return userInfoDTO;
    }
}
