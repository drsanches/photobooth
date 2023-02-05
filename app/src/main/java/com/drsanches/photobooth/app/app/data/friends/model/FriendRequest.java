package com.drsanches.photobooth.app.app.data.friends.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name="friend_requests")
public class FriendRequest {

    @EmbeddedId
    private FriendRequestKey id;

    public FriendRequest(String fromUserId, String toUserId) {
        this.id = new FriendRequestKey(fromUserId, toUserId);
    }

    public String getFromUserId() {
        return id.getFromUserId();
    }

    public String getToUser() {
        return id.getToUserId();
    }
}
