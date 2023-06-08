package com.drsanches.photobooth.app.app.data.profile.mapper;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.profile.dto.response.RelationshipDto;
import com.drsanches.photobooth.app.app.data.profile.dto.response.UserInfoDto;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInfoMapper {

    @Autowired
    private TokenSupplier tokenSupplier;

    public UserInfoDto convertFriend(UserProfile userProfile) {
        return convert(userProfile, RelationshipDto.FRIEND);
    }

    public UserInfoDto convertIncoming(UserProfile userProfile) {
        return convert(userProfile, RelationshipDto.INCOMING_FRIEND_REQUEST);
    }

    public UserInfoDto convertOutgoing(UserProfile userProfile) {
        return convert(userProfile, RelationshipDto.OUTGOING_FRIEND_REQUEST);
    }

    public UserInfoDto convert(UserProfile userProfile, RelationshipDto relationship) {
        UserInfoDto userInfoDto = convert(userProfile);
        userInfoDto.setRelationship(relationship);
        return userInfoDto;
    }

    public UserInfoDto convert(UserProfile userProfile, List<String> incomingIds, List<String> outgoingIds) {
        UserInfoDto userInfoDto = convert(userProfile);
        userInfoDto.setRelationship(getRelationship(userInfoDto.getId(), incomingIds, outgoingIds));
        return userInfoDto;
    }

    private UserInfoDto convert(UserProfile userProfile) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setId(userProfile.getId());
        if (userProfile.isEnabled()) {
            userInfoDto.setUsername(userProfile.getUsername());
            userInfoDto.setName(userProfile.getName());
            userInfoDto.setStatus(userProfile.getStatus());
        }
        userInfoDto.setImagePath(userProfile.isEnabled() ?
                userProfile.getImageId() == null ?
                        ImageConsts.IMAGE_PATH_PREFIX + ImageConsts.DEFAULT_AVATAR_ID :
                        ImageConsts.IMAGE_PATH_PREFIX + userProfile.getImageId() :
                ImageConsts.IMAGE_PATH_PREFIX + ImageConsts.DELETED_AVATAR_ID);
        userInfoDto.setThumbnailPath(userProfile.isEnabled() ?
                userProfile.getImageId() == null ?
                        ImageConsts.THUMBNAIL_PATH_PREFIX + ImageConsts.DEFAULT_AVATAR_ID :
                        ImageConsts.THUMBNAIL_PATH_PREFIX + userProfile.getImageId() :
                ImageConsts.THUMBNAIL_PATH_PREFIX + ImageConsts.DELETED_AVATAR_ID);
        return userInfoDto;
    }

    private RelationshipDto getRelationship(String userId, List<String> incomingIds, List<String> outgoingIds) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId.equals(userId)) {
            return RelationshipDto.CURRENT;
        }
        if (incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDto.FRIEND;
        } else if (incomingIds.contains(userId) && !outgoingIds.contains(userId)) {
            return RelationshipDto.INCOMING_FRIEND_REQUEST;
        } else if (!incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDto.OUTGOING_FRIEND_REQUEST;
        }
        return RelationshipDto.STRANGER;
    }
}
