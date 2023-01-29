package com.drsanches.photobooth.app.app.data.profile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name="user_profile")
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
