package com.drsanches.photobooth.app.app.data.friends.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FriendRequestKey implements Serializable {

    @Column(name = "fromUserId", nullable = false)
    private String fromUserId;

    @Column(name = "toUserId", nullable = false)
    private String toUserId;
}
