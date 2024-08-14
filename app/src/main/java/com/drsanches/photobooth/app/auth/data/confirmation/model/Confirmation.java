package com.drsanches.photobooth.app.auth.data.confirmation.model;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
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

import java.util.GregorianCalendar;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="confirmation", indexes = {
        @Index(name = "confirmation_id_index", columnList = "id"),
        @Index(name = "confirmation_code_index", columnList = "code"),
        @Index(name = "confirmation_expires_index", columnList = "expires")
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

    @Column(unique = true)
    private String newUsername;

    @Column(unique = true)
    private String newEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column
    @ToString.Exclude
    private String data;

    @Column(nullable = false)
    @ToString.Exclude
    private GregorianCalendar expires;

    @ToString.Include
    private String expires() {
        return GregorianCalendarConvertor.convert(expires);
    }
}
