package com.drsanches.photobooth.app.auth.data.userauth.model;

import com.drsanches.photobooth.app.common.token.data.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name="user_auth")
public class UserAuth {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    @ToString.Exclude
    private String password;

    @Column
    @ToString.Exclude
    private String salt;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String googleAuth;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
