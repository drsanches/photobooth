package ru.drsanches.photobooth.auth.data.confirmation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

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
@Table(name="confirmation")
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
