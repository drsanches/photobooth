package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.dto.friends.request.RemoveRequestDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.dto.friends.request.SendRequestDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class FriendsWebService {

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private AuthInfo authInfo;

    @Autowired
    private PaginationService<UserProfile> paginationService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public List<UserInfoDto> getFriends(Integer page, Integer size) {
        var userId = authInfo.getUserId();
        var friends = friendsDomainService.findOnlyFriendIds(userId);
        var result = userProfileDomainService.findAllByIdsOrderByUsername(friends).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertFriend)
                .toList();
    }

    public List<UserInfoDto> getIncomingRequests(Integer page, Integer size) {
        var userId = authInfo.getUserId();
        var incoming = friendsDomainService.findOnlyIncomingRequestIds(userId);
        var result = userProfileDomainService.findAllByIdsOrderByUsername(incoming).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertIncoming)
                .toList();
    }

    public List<UserInfoDto> getOutgoingRequests(Integer page, Integer size) {
        var userId = authInfo.getUserId();
        var outgoing = friendsDomainService.findOnlyOutgoingRequestIds(userId);
        var result = userProfileDomainService.findAllByIdsOrderByUsername(outgoing).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertOutgoing)
                .toList();
    }

    public void sendRequest(@Valid SendRequestDto sendRequestDto) {
        var fromUserId = authInfo.getUserId();
        friendsDomainService.create(fromUserId, sendRequestDto.getUserId());
        log.info("Friend request sent. FromUserId: {}, toUserId: {}", fromUserId, sendRequestDto.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDto removeRequestDto) {
        var currentUserId = authInfo.getUserId();
        friendsDomainService.delete(currentUserId, removeRequestDto.getUserId());
        log.info("Friendship canceled. ByUserId: {}, forUserId: {}", currentUserId, removeRequestDto.getUserId());
    }
}
