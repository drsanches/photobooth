package ru.drsanches.photobooth.auth.data.confirmation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Getter
@Setter
@ToString
@Entity
@Table(name="confirmation")
public class Confirmation {

    //TODO: Add userId and operation name and check it in web service
    //TODO: Use a specialized id field

    @Id
    @Column
    private String code;

    @Column(nullable = false)
    private String data;

    @Column(nullable = false)
    private GregorianCalendar expiresAt;
}
