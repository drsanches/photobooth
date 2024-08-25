package com.drsanches.photobooth.app.app.mapper;

import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.dto.profile.response.RelationshipDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserInfoMapper {

    @Autowired
    private AuthInfo authInfo;

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

    public UserInfoDto convert(UserProfile userProfile, FriendsDomainService.Relationships relationships) {
        return convert(userProfile).toBuilder()
                .relationship(getRelationship(userProfile.getId(), relationships))
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
                            ImageConsts.IMAGE_PATH.apply(ImageConsts.DEFAULT_PROFILE_PHOTO_ID) :
                            ImageConsts.IMAGE_PATH.apply(userProfile.getImageId()))
                    .thumbnailPath(userProfile.getImageId() == null ?
                            ImageConsts.THUMBNAIL_PATH.apply(ImageConsts.DEFAULT_PROFILE_PHOTO_ID) :
                            ImageConsts.THUMBNAIL_PATH.apply(userProfile.getImageId()))
                    .build();
        }
        return UserInfoDto.builder()
                .id(userProfile.getId())
                .imagePath(ImageConsts.IMAGE_PATH.apply(ImageConsts.DELETED_PROFILE_PHOTO_ID))
                .thumbnailPath(ImageConsts.THUMBNAIL_PATH.apply(ImageConsts.DELETED_PROFILE_PHOTO_ID))
                .build();
    }

    private RelationshipDto getRelationship(String userId, FriendsDomainService.Relationships relationships) {
        if (authInfo.getUserId().equals(userId)) {
            return RelationshipDto.CURRENT;
        }
        if (relationships.friends().contains(userId)) {
            return RelationshipDto.FRIEND;
        }
        if (relationships.incoming().contains(userId)) {
            return RelationshipDto.INCOMING_FRIEND_REQUEST;
        }
        if (relationships.outgoing().contains(userId)) {
            return RelationshipDto.OUTGOING_FRIEND_REQUEST;
        }
        return RelationshipDto.STRANGER;
    }
}
