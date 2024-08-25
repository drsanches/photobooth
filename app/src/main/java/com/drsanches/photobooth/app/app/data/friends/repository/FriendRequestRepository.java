package com.drsanches.photobooth.app.app.data.friends.repository;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@MonitorTime
public interface FriendRequestRepository extends CrudRepository<FriendRequest, FriendRequestKey> {

    @Override
    @NonNull
    <S extends FriendRequest> S save(@NonNull S entity);

    Set<FriendRequest> findByIdFromUserIdOrIdToUserId(String fromUserId, String toUserId);

    @Query("""
            SELECT f1.id.toUserId FROM FriendRequest f1 WHERE
            	f1.id.fromUserId = ?1 AND
            	f1.id.toUserId NOT IN (SELECT f2.id.fromUserId FROM FriendRequest f2 WHERE f2.id.toUserId = ?1)
            """)
    Set<String> findOutgoing(String userId);

    @Query("""
            SELECT f1.id.fromUserId FROM FriendRequest f1 WHERE
            	f1.id.toUserId = ?1 AND
            	f1.id.fromUserId NOT IN (SELECT f2.id.toUserId FROM FriendRequest f2 WHERE f2.id.fromUserId = ?1)
            """)
    Set<String> findIncoming(String userId);

    @Query("""
            SELECT f1.id.fromUserId FROM FriendRequest f1 WHERE
            	f1.id.toUserId = ?1 AND
            	f1.id.fromUserId IN (SELECT f2.id.toUserId FROM FriendRequest f2 WHERE f2.id.fromUserId = ?1)
            """)
    Set<String> findFriends(String userId);

    @Override
    void deleteById(@NonNull FriendRequestKey friendRequestKey);
}
