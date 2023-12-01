package com.drsanches.photobooth.app.notifier.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="notification_info", indexes = {
        @Index(name = "notification_info_id_index", columnList = "id"),
        @Index(name = "notification_info_user_id_index", columnList = "userId")
        //TODO: Add composite index
})
public class NotificationInfo {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String target;

    @Column(nullable = false)
    private NotificationType type;
}
