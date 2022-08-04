package ru.drsanches.photobooth.app.data.friends.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.photobooth.app.data.friends.model.FriendRequest;
import ru.drsanches.photobooth.app.data.friends.model.FriendRequestKey;
import java.util.Set;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, FriendRequestKey> {

    Set<FriendRequest> findByIdFromUserId(String fromUserId);

    Set<FriendRequest> findByIdToUserId(String toUserId);
}