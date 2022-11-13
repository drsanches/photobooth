package ru.drsanches.photobooth.app.data.profile.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.mapper.ImageInfoMapper;
import ru.drsanches.photobooth.app.data.profile.dto.response.Relationship;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;

@Component
public class UserInfoMapper {

    public UserInfoDTO convertFriend(UserProfile userProfile) {
        return convert(userProfile, Relationship.FRIEND);
    }

    public UserInfoDTO convertIncoming(UserProfile userProfile) {
        return convert(userProfile, Relationship.INCOMING_FRIEND_REQUEST);
    }

    public UserInfoDTO convertOutgoing(UserProfile userProfile) {
        return convert(userProfile, Relationship.OUTGOING_FRIEND_REQUEST);
    }

    public UserInfoDTO convert(UserProfile userProfile, Relationship relationship) {
        UserInfoDTO userInfoDTO = convert(userProfile);
        userInfoDTO.setRelationship(relationship);
        return userInfoDTO;
    }

    private UserInfoDTO convert(UserProfile userProfile) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userProfile.getId());
        if (userProfile.isEnabled()) {
            userInfoDTO.setUsername(userProfile.getUsername());
            userInfoDTO.setName(userProfile.getName());
            userInfoDTO.setStatus(userProfile.getStatus());
        }
        userInfoDTO.setImagePath(userProfile.isEnabled() ?
                userProfile.getImageId() == null ?
                        ImageInfoMapper.IMAGE_PATH_PREFIX + ImageInfoMapper.DEFAULT_AVATAR_ID :
                        ImageInfoMapper.IMAGE_PATH_PREFIX + userProfile.getImageId() :
                ImageInfoMapper.IMAGE_PATH_PREFIX + ImageInfoMapper.DELETED_AVATAR_ID);
        userInfoDTO.setThumbnailPath(userProfile.isEnabled() ?
                userProfile.getImageId() == null ?
                        ImageInfoMapper.THUMBNAIL_PATH_PREFIX + ImageInfoMapper.DEFAULT_AVATAR_ID :
                        ImageInfoMapper.THUMBNAIL_PATH_PREFIX + userProfile.getImageId() :
                ImageInfoMapper.THUMBNAIL_PATH_PREFIX + ImageInfoMapper.DELETED_AVATAR_ID);
        return userInfoDTO;
    }
}
