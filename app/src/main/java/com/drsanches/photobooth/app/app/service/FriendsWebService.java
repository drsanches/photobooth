package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.dto.friends.request.RemoveRequestDto;
import com.drsanches.photobooth.app.app.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.data.friends.FriendsDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.app.utils.PaginationService;
import com.drsanches.photobooth.app.app.dto.friends.request.SendRequestDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Validated
public class FriendsWebService {

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private PaginationService<UserProfile> paginationService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public List<UserInfoDto> getFriends(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> friends = friendsDomainService.getFriendsIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(friends).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertFriend)
                .collect(Collectors.toList());
    }

    public List<UserInfoDto> getIncomingRequests(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> incoming = friendsDomainService.getIncomingRequestIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(incoming).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertIncoming)
                .collect(Collectors.toList());
    }

    public List<UserInfoDto> getOutgoingRequests(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> outgoing = friendsDomainService.getOutgoingRequestIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(outgoing).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertOutgoing)
                .collect(Collectors.toList());
    }

    public void sendRequest(@Valid SendRequestDto sendRequestDto) {
        String fromUserId = tokenSupplier.get().getUserId();
        friendsDomainService.saveFriendRequest(fromUserId, sendRequestDto.getUserId());
        log.info("Friend request sent. FromUserId: {}, toUserId: {}", fromUserId, sendRequestDto.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDto removeRequestDto) {
        String currentUserId = tokenSupplier.get().getUserId();
        friendsDomainService.removeFriendRequest(currentUserId, removeRequestDto.getUserId());
        log.info("Friendship canceled. ByUserId: {}, forUserId: {}", currentUserId, removeRequestDto.getUserId());
    }
}
