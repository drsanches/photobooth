package com.drsanches.photobooth.app.app.data.friends;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import com.drsanches.photobooth.app.app.data.friends.repository.FriendRequestRepository;
import com.drsanches.photobooth.app.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//TODO: Refactor - too many db requests
@Slf4j
@Service
public class FriendsDomainService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public void saveFriendRequest(String fromUserId, String toUserId) {
        FriendRequest friendRequest = new FriendRequest(fromUserId, toUserId);
        friendRequestRepository.save(friendRequest);
        log.debug("New FriendRequest created: {}", friendRequest);
    }

    public List<String> getFriendsIdList(String userId) {
        List<String> outgoing = getOutgoingRequestAndFriendIdList(userId);
        List<String> incoming = getIncomingRequestAndFriendIdList(userId);
        return incoming.stream()
                .filter(outgoing::contains)
                .collect(Collectors.toList());
    }

    public List<String> getIncomingRequestIdList(String userId) {
        List<String> outgoing = getOutgoingRequestAndFriendIdList(userId);
        List<String> incoming = getIncomingRequestAndFriendIdList(userId);
        return incoming.stream()
                .filter(x -> !outgoing.contains(x))
                .collect(Collectors.toList());
    }

    public List<String> getOutgoingRequestIdList(String userId) {
        List<String> outgoing = getOutgoingRequestAndFriendIdList(userId);
        List<String> incoming = getIncomingRequestAndFriendIdList(userId);
        return outgoing.stream()
                .filter(x -> !incoming.contains(x))
                .collect(Collectors.toList());
    }

    public List<String> getOutgoingRequestAndFriendIdList(String userId) {
        return friendRequestRepository.findByIdFromUserId(userId).stream()
                .map(FriendRequest::getToUser)
                .collect(Collectors.toList());
    }

    public List<String> getIncomingRequestAndFriendIdList(String userId) {
        return friendRequestRepository.findByIdToUserId(userId).stream()
                .map(FriendRequest::getFromUserId)
                .collect(Collectors.toList());
    }

    public void removeFriendRequest(String fromUserId, String toUserId) {
        FriendRequestKey friendRequestKey = new FriendRequestKey(fromUserId, toUserId);
        try {
            friendRequestRepository.deleteById(friendRequestKey);
            log.debug("FriendRequest removed: {}", friendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.warn("FriendRequest does not exist. Key: {}", friendRequestKey, e);
        }
        FriendRequestKey reversedFriendRequestKey = new FriendRequestKey(toUserId, fromUserId);
        try {
            friendRequestRepository.deleteById(reversedFriendRequestKey);
            log.debug("Reversed FriendRequest removed: {}", reversedFriendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.debug("Reversed FriendRequest does not exist. Key: {}", reversedFriendRequestKey, e);
        }
    }
}
