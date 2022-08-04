package ru.drsanches.photobooth.app.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.friends.model.FriendRequest;
import ru.drsanches.photobooth.app.data.friends.model.FriendRequestKey;
import ru.drsanches.photobooth.app.data.friends.repository.FriendRequestRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsDomainService {

    private final Logger LOG = LoggerFactory.getLogger(FriendsDomainService.class);

    @Autowired
    private FriendRequestRepository friendRequestRepository;

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

    public void saveFriendRequest(String fromUserId, String toUserId) {
        FriendRequest friendRequest = new FriendRequest(fromUserId, toUserId);
        friendRequestRepository.save(friendRequest);
        LOG.info("New FriendRequest has been created: {}", friendRequest);
    }

    public void removeFriendRequest(String fromUserId, String toUserId) {
        FriendRequestKey friendRequestKey = new FriendRequestKey(fromUserId, toUserId);
        try {
            friendRequestRepository.deleteById(friendRequestKey);
            LOG.info("FriendRequest has been removed: {}", friendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            LOG.warn("FriendRequest does not exist: " + friendRequestKey, e);
        }
        FriendRequestKey reversedFriendRequestKey = new FriendRequestKey(toUserId, fromUserId);
        try {
            friendRequestRepository.deleteById(reversedFriendRequestKey);
            LOG.info("Reversed FriendRequest has been removed: {}", reversedFriendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            LOG.info("Reversed FriendRequest does not exist: " + reversedFriendRequestKey, e);
        }
    }

    private List<String> getOutgoingRequestAndFriendIdList(String userId) {
        return friendRequestRepository.findByIdFromUserId(userId).stream()
                .map(FriendRequest::getToUser)
                .collect(Collectors.toList());
    }

    private List<String> getIncomingRequestAndFriendIdList(String userId) {
        return friendRequestRepository.findByIdToUserId(userId).stream()
                .map(FriendRequest::getFromUserId)
                .collect(Collectors.toList());
    }
}