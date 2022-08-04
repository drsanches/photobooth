package ru.drsanches.photobooth.app.data.friends.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="friend_requests")
public class FriendRequest {

    @EmbeddedId
    private FriendRequestKey id;

    public FriendRequest() {}

    public FriendRequest(String fromUserId, String toUserId) {
        this.id = new FriendRequestKey(fromUserId, toUserId);
    }

    public String getFromUserId() {
        return id.getFromUserId();
    }

    public String getToUser() {
        return id.getToUserId();
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "id=" + id +
                '}';
    }
}