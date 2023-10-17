package com.drsanches.photobooth.app.app.mapper;

import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.dto.profile.response.RelationshipDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInfoMapper {

    @Autowired
    private TokenSupplier tokenSupplier;

    public UserInfoDto convertCurrent(
            UserProfile userProfile,
            int incomingRequestsCount,
            int outgoingRequestsCount,
            int friendsCount
    ) {
        return convert(userProfile).toBuilder()
                .relationship(RelationshipDto.CURRENT)
                .incomingRequestsCount(incomingRequestsCount)
                .outgoingRequestsCount(outgoingRequestsCount)
                .friendsCount(friendsCount)
                .build();
    }

    public UserInfoDto convertFriend(UserProfile userProfile) {
        return convert(userProfile).toBuilder()
                .relationship(RelationshipDto.FRIEND)
                .build();
    }

    public UserInfoDto convertIncoming(UserProfile userProfile) {
        return convert(userProfile).toBuilder()
                .relationship(RelationshipDto.INCOMING_FRIEND_REQUEST)
                .build();
    }

    public UserInfoDto convertOutgoing(UserProfile userProfile) {
        return convert(userProfile).toBuilder()
                .relationship(RelationshipDto.OUTGOING_FRIEND_REQUEST)
                .build();
    }

    public UserInfoDto convert(UserProfile userProfile, List<String> incomingIds, List<String> outgoingIds) {
        return convert(userProfile).toBuilder()
                .relationship(getRelationship(userProfile.getId(), incomingIds, outgoingIds))
                .build();
    }

    private UserInfoDto convert(UserProfile userProfile) {
        if (userProfile.isEnabled()) {
            return UserInfoDto.builder()
                    .id(userProfile.getId())
                    .username(userProfile.getUsername())
                    .name(userProfile.getName())
                    .status(userProfile.getStatus())
                    .imagePath(userProfile.getImageId() == null ?
                            ImageConsts.IMAGE_PATH.apply(ImageConsts.DEFAULT_AVATAR_ID) :
                            ImageConsts.IMAGE_PATH.apply(userProfile.getImageId()))
                    .thumbnailPath(userProfile.getImageId() == null ?
                            ImageConsts.THUMBNAIL_PATH.apply(ImageConsts.DEFAULT_AVATAR_ID) :
                            ImageConsts.THUMBNAIL_PATH.apply(userProfile.getImageId()))
                    .build();
        }
        return UserInfoDto.builder()
                .id(userProfile.getId())
                .imagePath(ImageConsts.IMAGE_PATH.apply(ImageConsts.DELETED_AVATAR_ID))
                .thumbnailPath(ImageConsts.THUMBNAIL_PATH.apply(ImageConsts.DELETED_AVATAR_ID))
                .build();
    }

    private RelationshipDto getRelationship(String userId, List<String> incomingIds, List<String> outgoingIds) {
        String currentUserId = tokenSupplier.get().getUserId();
        if (currentUserId.equals(userId)) {
            return RelationshipDto.CURRENT;
        }
        if (incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDto.FRIEND;
        }
        if (incomingIds.contains(userId) && !outgoingIds.contains(userId)) {
            return RelationshipDto.INCOMING_FRIEND_REQUEST;
        }
        if (!incomingIds.contains(userId) && outgoingIds.contains(userId)) {
            return RelationshipDto.OUTGOING_FRIEND_REQUEST;
        }
        return RelationshipDto.STRANGER;
    }
}
