package com.drsanches.photobooth.app.auth.data.userauth.model;

import com.drsanches.photobooth.app.common.token.data.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_auth", indexes = {
        @Index(name = "user_auth_id_index", columnList = "id"),
        @Index(name = "user_auth_username_index", columnList = "username"),
        @Index(name = "user_auth_email_index", columnList = "email"),
        @Index(name = "user_auth_google_auth_index", columnList = "googleAuth")
})
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
