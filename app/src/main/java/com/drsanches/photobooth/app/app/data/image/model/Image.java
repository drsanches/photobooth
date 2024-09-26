package com.drsanches.photobooth.app.app.data.image.model;

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

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="image", indexes = {
        @Index(name = "image_id_index", columnList = "id"),
        @Index(name = "image_id_and_created_time_index", columnList = "id, created")
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
    private Instant created;

    @Column(nullable = false)
    private String ownerId;

    @Column(precision = 8, scale = 6)
    private BigDecimal lat;

    @Column(precision = 9, scale = 6)
    private BigDecimal lng;

    @ToString.Include
    private int dataLength() {
        return data.length;
    }
}
