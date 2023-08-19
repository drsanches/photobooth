package com.drsanches.photobooth.app.app.data.friends.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="friend_requests", indexes = {
        @Index(name = "friend_requests_from_user_id_index", columnList = "fromUserId"),
        @Index(name = "friend_requests_to_user_id_index", columnList = "toUserId")
})
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
