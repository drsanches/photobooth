package com.drsanches.photobooth.app.app.data.image.model;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="image", indexes = {
        @Index(name = "image_id_index", columnList = "id"),
        @Index(name = "image_id_and_created_time_index", columnList = "id, createdTime")
})
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
