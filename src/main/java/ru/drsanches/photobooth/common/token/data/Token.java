package ru.drsanches.photobooth.common.token.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Getter
@Setter
@ToString
@Entity
@Table(name="token")
public class Token {

    @Id
    @Column
    @ToString.Exclude
    private String accessToken;

    @Column(unique = true, nullable = false)
    @ToString.Exclude
    private String refreshToken;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    @ToString.Exclude
    private GregorianCalendar expiresAt;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
