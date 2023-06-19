package com.drsanches.photobooth.app.app.data.friends.repository;

import com.drsanches.photobooth.app.app.data.friends.model.FriendRequest;
import com.drsanches.photobooth.app.app.data.friends.model.FriendRequestKey;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
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

    Set<FriendRequest> findByIdFromUserId(String fromUserId);

    Set<FriendRequest> findByIdToUserId(String toUserId);

    @Override
    void deleteById(@NonNull FriendRequestKey friendRequestKey);
}
