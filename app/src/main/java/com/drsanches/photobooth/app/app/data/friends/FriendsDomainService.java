package com.drsanches.photobooth.app.app.data.friends;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import com.drsanches.photobooth.app.app.data.friends.repository.FriendRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FriendsDomainService {

    public record Relationships(List<String> incoming, List<String> outgoing, List<String> friends) {}

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public void create(String fromUserId, String toUserId) {
        var friendRequest = new FriendRequest(fromUserId, toUserId);
        friendRequestRepository.save(friendRequest);
        log.info("New FriendRequest created: {}", friendRequest);
    }

    public int getIncomingRequestsCount(String userId) {
        return findOnlyIncomingRequestIds(userId).size();
    }

    public int getOutgoingRequestsCount(String userId) {
        return findOnlyOutgoingRequestIds(userId).size();
    }

    public int getFriendsCount(String userId) {
        return findOnlyFriendIds(userId).size();
    }

    public Set<String> findOnlyFriendIds(String userId) {
        return friendRequestRepository.findFriends(userId);
    }

    public Set<String> findOnlyIncomingRequestIds(String userId) {
        return friendRequestRepository.findIncoming(userId);
    }

    public Set<String> findOnlyOutgoingRequestIds(String userId) {
        return friendRequestRepository.findOutgoing(userId);
    }

    public Relationships findAllRelationships(String userId) {
        record Pair(boolean incoming, boolean outgoing) {}

        var all = friendRequestRepository.findByIdFromUserIdOrIdToUserId(userId, userId);
        Map<String, Pair> map = new HashMap<>();

        all.forEach(it -> {
            if (it.getToUser().equals(userId)) {
                var another = it.getFromUserId();
                if (!map.containsKey(another)) {
                    map.put(another, new Pair(true, false));
                } else {
                    map.put(another, new Pair(true, map.get(another).outgoing()));
                }
            } else if (it.getFromUserId().equals(userId)) {
                var another = it.getToUser();
                if (!map.containsKey(another)) {
                    map.put(another, new Pair(false, true));
                } else {
                    map.put(another, new Pair(map.get(another).incoming(), true));
                }
            }
        });

        var incoming = new ArrayList<String>();
        var outgoing = new ArrayList<String>();
        var friends = new ArrayList<String>();

        map.forEach((key, pair) -> {
            if (pair.incoming() && !pair.outgoing()) {
                incoming.add(key);
            } else if (pair.outgoing() && !pair.incoming()) {
                outgoing.add(key);
            } else {
                friends.add(key);
            }
        });
        return new Relationships(incoming, outgoing, friends);
    }

    public void delete(String fromUserId, String toUserId) {
        var friendRequestKey = new FriendRequestKey(fromUserId, toUserId);
        try {
            friendRequestRepository.deleteById(friendRequestKey);
            log.info("FriendRequest deleted: {}", friendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.warn("FriendRequest does not exist. Key: {}", friendRequestKey, e);
        }
        var reversedFriendRequestKey = new FriendRequestKey(toUserId, fromUserId);
        try {
            friendRequestRepository.deleteById(reversedFriendRequestKey);
            log.info("Reversed FriendRequest deleted: {}", reversedFriendRequestKey);
        } catch(EmptyResultDataAccessException e) {
            log.info("Reversed FriendRequest does not exist. Key: {}", reversedFriendRequestKey, e);
        }
    }
}
