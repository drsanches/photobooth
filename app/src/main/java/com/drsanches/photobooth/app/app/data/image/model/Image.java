package com.drsanches.photobooth.app.app.data.image.model;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
