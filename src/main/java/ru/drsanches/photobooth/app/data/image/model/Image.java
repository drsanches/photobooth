package ru.drsanches.photobooth.app.data.image.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="image")
public class Image {

    @Id
    @Column
    private String id;

    @Column(nullable = false)
    @ToString.Exclude
    private byte[] data;

    @Column(nullable = false)
    @ToString.Exclude
    private byte[] thumbnailData;

    @Column(nullable = false)
    @ToString.Exclude
    private GregorianCalendar createdTime;

    @Column(nullable = false)
    private String ownerId;

    @ToString.Include
    private String createdTime() {
        return GregorianCalendarConvertor.convert(createdTime);
    }

    @ToString.Include
    private int dataLength() {
        return data.length;
    }
}
