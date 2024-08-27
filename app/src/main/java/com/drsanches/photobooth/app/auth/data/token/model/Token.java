package com.drsanches.photobooth.app.auth.data.token.model;

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

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="token", indexes = {
        @Index(name = "token_id_index", columnList = "id"),
        @Index(name = "token_access_token_index", columnList = "accessToken"),
        @Index(name = "token_refresh_token_index", columnList = "refreshToken"),
        @Index(name = "token_user_id_index", columnList = "userId"),
        @Index(name = "token_expires_and_refresh_expires_index", columnList = "expires, refreshExpires")
})
public class Token {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    @ToString.Exclude
    private String accessToken;

    @Column(unique = true, nullable = false)
    @ToString.Exclude
    private String refreshToken;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    @ToString.Exclude
    private Instant expires;

    @Column(nullable = false)
    @ToString.Exclude
    private Instant refreshExpires;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
