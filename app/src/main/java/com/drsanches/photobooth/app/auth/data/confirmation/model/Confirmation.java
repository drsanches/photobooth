package com.drsanches.photobooth.app.auth.data.confirmation.model;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
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
import java.util.GregorianCalendar;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="confirmation", indexes = {
        @Index(name = "confirmation_id_index", columnList = "id"),
        @Index(name = "confirmation_code_index", columnList = "code"),
        @Index(name = "confirmation_expires_index", columnList = "expiresAt")
})
public class Confirmation {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    @ToString.Exclude
    private String code;

    @Column
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column
    @ToString.Exclude
    private String data;

    @Column(nullable = false)
    @ToString.Exclude
    private GregorianCalendar expiresAt;

    @ToString.Include
    private String expiresAt() {
        return GregorianCalendarConvertor.convert(expiresAt);
    }
}
