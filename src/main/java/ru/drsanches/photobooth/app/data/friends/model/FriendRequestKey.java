package ru.drsanches.photobooth.app.data.friends.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class FriendRequestKey implements Serializable {

    @Column(name = "fromUserId", nullable = false)
    private String fromUserId;

    @Column(name = "toUserId", nullable = false)
    private String toUserId;
}
