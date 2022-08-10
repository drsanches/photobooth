package ru.drsanches.photobooth.app.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.app.data.friends.dto.RemoveRequestDTO;
import ru.drsanches.photobooth.app.data.friends.dto.SendRequestDTO;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.photobooth.app.service.domain.FriendsDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import javax.validation.Valid;
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
    private UserInfoMapper userInfoMapper;

    @Autowired
    private TokenSupplier tokenSupplier;

    public List<UserInfoDTO> getFriends() {
        String userId = tokenSupplier.get().getUserId();
        List<String> friends = friendsDomainService.getFriendsIdList(userId);
        return userProfileDomainService.getAllByIds(friends).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getIncomingRequests() {
        String userId = tokenSupplier.get().getUserId();
        List<String> incoming = friendsDomainService.getIncomingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(incoming).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getOutgoingRequests() {
        String userId = tokenSupplier.get().getUserId();
        List<String> outgoing = friendsDomainService.getOutgoingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(outgoing).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public void sendRequest(@Valid SendRequestDTO sendRequestDTO) {
        String fromUserId = tokenSupplier.get().getUserId();
        friendsDomainService.saveFriendRequest(fromUserId, sendRequestDTO.getUserId());
        log.info("User with id '{}' send friend request to user '{}'", fromUserId, sendRequestDTO.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDTO removeRequestDTO) {
        String currentUserId = tokenSupplier.get().getUserId();
        friendsDomainService.removeFriendRequest(currentUserId, removeRequestDTO.getUserId());
        log.info("User with id '{}' canceled friendship for user '{}'", currentUserId, removeRequestDTO.getUserId());
    }
}
