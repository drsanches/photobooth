package com.drsanches.photobooth.app.app.data.friends.repository;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, FriendRequestKey> {

    Set<FriendRequest> findByIdFromUserId(String fromUserId);

    Set<FriendRequest> findByIdToUserId(String toUserId);
}
