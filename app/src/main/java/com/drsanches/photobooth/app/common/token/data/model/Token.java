package com.drsanches.photobooth.app.common.token.data.model;

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
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="token")
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
    private GregorianCalendar expires;

    @Column(nullable = false)
    @ToString.Exclude
    private GregorianCalendar refreshExpires;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
