package com.drsanches.photobooth.app.auth.data.userauth.model;

import com.drsanches.photobooth.app.common.token.data.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_auth", indexes = {
        @Index(name = "user_auth_id_index", columnList = "id"),
        @Index(name = "user_auth_username_index", columnList = "username"),
        @Index(name = "user_auth_email_index", columnList = "email"),
        @Index(name = "user_auth_google_auth_index", columnList = "googleAuth"),
        @Index(name = "user_auth_username_and_enabled_index", columnList = "username, enabled"),
        @Index(name = "user_auth_email_and_enabled_index", columnList = "email, enabled"),
        @Index(name = "user_auth_google_auth_and_enabled_index", columnList = "googleAuth, enabled")
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
