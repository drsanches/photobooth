package com.drsanches.photobooth.app.app.service.web;

import com.drsanches.photobooth.app.app.data.friends.dto.request.RemoveRequestDTO;
import com.drsanches.photobooth.app.app.data.profile.mapper.UserInfoMapper;
import com.drsanches.photobooth.app.app.data.profile.model.UserProfile;
import com.drsanches.photobooth.app.app.service.domain.FriendsDomainService;
import com.drsanches.photobooth.app.app.service.domain.UserProfileDomainService;
import com.drsanches.photobooth.app.app.service.utils.PaginationService;
import com.drsanches.photobooth.app.app.data.friends.dto.request.SendRequestDTO;
import com.drsanches.photobooth.app.app.data.profile.dto.response.UserInfoDTO;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
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

    public List<UserInfoDTO> getFriends(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> friends = friendsDomainService.getFriendsIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(friends).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertFriend)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getIncomingRequests(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> incoming = friendsDomainService.getIncomingRequestIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(incoming).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertIncoming)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getOutgoingRequests(Integer page, Integer size) {
        String userId = tokenSupplier.get().getUserId();
        List<String> outgoing = friendsDomainService.getOutgoingRequestIdList(userId);
        Stream<UserProfile> result = userProfileDomainService.getAllByIdsOrderByUsername(outgoing).stream();
        return paginationService.pagination(result, page, size)
                .map(userInfoMapper::convertOutgoing)
                .collect(Collectors.toList());
    }

    public void sendRequest(@Valid SendRequestDTO sendRequestDTO) {
        String fromUserId = tokenSupplier.get().getUserId();
        friendsDomainService.saveFriendRequest(fromUserId, sendRequestDTO.getUserId());
        log.info("Friend request sent. FromUserId: {}, toUserId: {}", fromUserId, sendRequestDTO.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDTO removeRequestDTO) {
        String currentUserId = tokenSupplier.get().getUserId();
        friendsDomainService.removeFriendRequest(currentUserId, removeRequestDTO.getUserId());
        log.info("Friendship canceled. ByUserId: {}, forUserId: {}", currentUserId, removeRequestDTO.getUserId());
    }
}
