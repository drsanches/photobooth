package com.drsanches.photobooth.app.notifier.data.fcm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="fcm_token", indexes = {
        @Index(name = "fcm_token_id_index", columnList = "id"),
        @Index(name = "fcm_token_user_id_index", columnList = "userId"),
        @Index(name = "fcm_token_token_index", columnList = "token"),
        @Index(name = "fcm_token_expires_index", columnList = "expires")
})
public class FcmToken {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    @ToString.Exclude
    private String token;

    @Column(nullable = false)
    @ToString.Exclude
    private Instant expires;
}
