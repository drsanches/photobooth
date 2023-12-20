package com.drsanches.photobooth.app.app.data.profile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_profile", indexes = {
        @Index(name = "user_profile_id_index", columnList = "id"),
        @Index(name = "user_profile_username_index", columnList = "username"),
        @Index(name = "user_profile_id_and_username_index", columnList = "id, username"),
        @Index(name = "user_profile_id_and_enabled_index", columnList = "id, enabled"),
        @Index(name = "user_profile_username_and_enabled_index", columnList = "username, enabled")
})
public class UserProfile {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private String name;

    //TODO: May be use enum
    @Column
    private String status;

    @Column
    private String imageId;
}
