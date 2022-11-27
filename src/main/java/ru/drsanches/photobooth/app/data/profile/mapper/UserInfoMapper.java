package ru.drsanches.photobooth.app.data.profile.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.profile.dto.response.RelationshipDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import ru.drsanches.photobooth.config.ImageConsts;

import java.util.List;

@Component
public class UserInfoMapper {

    @Autowired
    private TokenSupplier tokenSupplier;

    public UserInfoDTO convertFriend(UserProfile userProfile) {
        return convert(userProfile, RelationshipDTO.FRIEND);
    }

    public UserInfoDTO convertIncoming(UserProfile userProfile) {
        return convert(userProfile, RelationshipDTO.INCOMING_FRIEND_REQUEST);
    }

    public UserInfoDTO convertOutgoing(UserProfile userProfile) {
        return convert(userProfile, RelationshipDTO.OUTGOING_FRIEND_REQUEST);
    }

    public UserInfoDTO convert(UserProfile userProfile, RelationshipDTO relationship) {
        UserInfoDTO userInfoDTO = convert(userProfile);
        userInfoDTO.setRelationship(relationship);
        return userInfoDTO;
    }

    public UserInfoDTO convert(UserProfile userProfile, List<String> incomingIds, List<String> outgoingIds) {
        UserInfoDTO userInfoDTO = convert(userProfile);
        userInfoDTO.setRelationship(getRelationship(userInfoDTO.getId(), incomingIds, outgoingIds));
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
                        ImageConsts.IMAGE_PATH_PREFIX + ImageConsts.DEFAULT_AVATAR_ID :
                        ImageConsts.IMAGE_PATH_PREFIX + userProfile.getImageId() :
                ImageConsts.IMAGE_PATH_PREFIX + ImageConsts.DELETED_AVATAR_ID);
        userInfoDTO.setThumbnailPath(userProfile.isEnabled() ?
                userProfile.getImageId() == null ?
                        ImageConsts.THUMBNAIL_PATH_PREFIX + ImageConsts.DEFAULT_AVATAR_ID :
                        ImageConsts.THUMBNAIL_PATH_PREFIX + userProfile.getImageId() :
                ImageConsts.THUMBNAIL_PATH_PREFIX + ImageConsts.DELETED_AVATAR_ID);
        return userInfoDTO;
    }

    private RelationshipDTO getRelationship(String userId, List<String> incomingIds, List<String> outgoingIds) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId.equals(userId)) {
            return RelationshipDTO.CURRENT;
        }
        if (incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDTO.FRIEND;
        } else if (incomingIds.contains(userId) && !outgoingIds.contains(userId)) {
            return RelationshipDTO.INCOMING_FRIEND_REQUEST;
        } else if (!incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDTO.OUTGOING_FRIEND_REQUEST;
        }
        return RelationshipDTO.STRANGER;
    }
}
