package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.config.UserInfo;
import com.drsanches.photobooth.app.app.dto.friends.request.RemoveRequestDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.dto.friends.request.SendRequestDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
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
    private UserInfo userInfo;

    @Autowired
    private PaginationService<UserProfile> paginationService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public List<UserInfoDto> getFriends(Integer page, Integer size) {
        var userId = userInfo.getUserId();
        var friends = friendsDomainService.getFriendsIds(userId);
        var result = userProfileDomainService.getAllByIdsOrderByUsername(friends).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertFriend)
                .collect(Collectors.toList());
    }

    public List<UserInfoDto> getIncomingRequests(Integer page, Integer size) {
        var userId = userInfo.getUserId();
        var incoming = friendsDomainService.getIncomingRequestIds(userId);
        var result = userProfileDomainService.getAllByIdsOrderByUsername(incoming).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertIncoming)
                .collect(Collectors.toList());
    }

    public List<UserInfoDto> getOutgoingRequests(Integer page, Integer size) {
        var userId = userInfo.getUserId();
        var outgoing = friendsDomainService.getOutgoingRequestIds(userId);
        var result = userProfileDomainService.getAllByIdsOrderByUsername(outgoing).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertOutgoing)
                .collect(Collectors.toList());
    }

    public void sendRequest(@Valid SendRequestDto sendRequestDto) {
        var fromUserId = userInfo.getUserId();
        friendsDomainService.saveFriendRequest(fromUserId, sendRequestDto.getUserId());
        log.info("Friend request sent. FromUserId: {}, toUserId: {}", fromUserId, sendRequestDto.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDto removeRequestDto) {
        var currentUserId = userInfo.getUserId();
        friendsDomainService.removeFriendRequest(currentUserId, removeRequestDto.getUserId());
        log.info("Friendship canceled. ByUserId: {}, forUserId: {}", currentUserId, removeRequestDto.getUserId());
    }
}
