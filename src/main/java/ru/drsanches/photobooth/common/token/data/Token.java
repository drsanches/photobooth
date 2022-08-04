package ru.drsanches.photobooth.common.token.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Entity
@Table(name="token")
public class Token {

    @Id
    @Column
    private String accessToken;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    private GregorianCalendar expiresAt;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Role role;

    public Token() {}

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public GregorianCalendar getExpiresAt() {
        return expiresAt;
    }

    public String getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresAt(GregorianCalendar expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType='" + tokenType + '\'' +
                ", userId='" + userId + '\'' +
                ", role=" + role +
                '}';
    }
}