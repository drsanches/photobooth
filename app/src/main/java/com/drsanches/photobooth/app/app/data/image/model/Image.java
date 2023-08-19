package com.drsanches.photobooth.app.app.data.image.model;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
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
