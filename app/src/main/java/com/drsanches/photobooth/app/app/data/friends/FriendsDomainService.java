package com.drsanches.photobooth.app.app.data.friends;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import com.drsanches.photobooth.app.app.data.friends.repository.FriendRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

//TODO: Refactor - too many db requests
@Slf4j
@Service
public class FriendsDomainService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public void saveFriendRequest(String fromUserId, String toUserId) {
        var friendRequest = new FriendRequest(fromUserId, toUserId);
        friendRequestRepository.save(friendRequest);
        log.debug("New FriendRequest created: {}", friendRequest);
    }

    public int getIncomingRequestsCount(String userId) {
        return getIncomingRequestIds(userId).size();
    }

    public int getOutgoingRequestsCount(String userId) {
        return getOutgoingRequestIds(userId).size();
    }

    public int getFriendsCount(String userId) {
        return getFriendsIds(userId).size();
    }

    public Set<String> getFriendsIds(String userId) {
        var outgoing = getOutgoingRequestAndFriendIds(userId);
        var incoming = getIncomingRequestAndFriendIds(userId);
        return incoming.stream()
                .filter(outgoing::contains)
                .collect(Collectors.toSet());
    }

    public Set<String> getIncomingRequestIds(String userId) {
        var outgoing = getOutgoingRequestAndFriendIds(userId);
        var incoming = getIncomingRequestAndFriendIds(userId);
        return incoming.stream()
                .filter(x -> !outgoing.contains(x))
                .collect(Collectors.toSet());
    }

    public Set<String> getOutgoingRequestIds(String userId) {
        var outgoing = getOutgoingRequestAndFriendIds(userId);
        var incoming = getIncomingRequestAndFriendIds(userId);
        return outgoing.stream()
                .filter(x -> !incoming.contains(x))
                .collect(Collectors.toSet());
    }

    public Set<String> getOutgoingRequestAndFriendIds(String userId) {
        return friendRequestRepository.findByIdFromUserId(userId).stream()
                .map(FriendRequest::getToUser)
                .collect(Collectors.toSet());
    }

    public Set<String> getIncomingRequestAndFriendIds(String userId) {
        return friendRequestRepository.findByIdToUserId(userId).stream()
                .map(FriendRequest::getFromUserId)
                .collect(Collectors.toSet());
    }

    public void removeFriendRequest(String fromUserId, String toUserId) {
        var friendRequestKey = new FriendRequestKey(fromUserId, toUserId);
        try {
            friendRequestRepository.deleteById(friendRequestKey);
            log.debug("FriendRequest removed: {}", friendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.warn("FriendRequest does not exist. Key: {}", friendRequestKey, e);
        }
        var reversedFriendRequestKey = new FriendRequestKey(toUserId, fromUserId);
        try {
            friendRequestRepository.deleteById(reversedFriendRequestKey);
            log.debug("Reversed FriendRequest removed: {}", reversedFriendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.debug("Reversed FriendRequest does not exist. Key: {}", reversedFriendRequestKey, e);
        }
    }
}
