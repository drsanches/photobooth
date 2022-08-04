package ru.drsanches.photobooth.app.data.friends.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FriendRequestKey implements Serializable {

    @Column(name = "fromUserId", nullable = false)
    private String fromUserId;

    @Column(name = "toUserId", nullable = false)
    private String toUserId;

    public FriendRequestKey() {}

    public FriendRequestKey(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    @Override
    public String toString() {
        return "FriendRequestKey{" +
                "fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                '}';
    }
}