package com.drsanches.photobooth.app.app.data.permission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ImagePermissionKey implements Serializable {

    @Column(name = "imageId", nullable = false)
    private String imageId;

    @Column(name = "userId", nullable = false)
    private String userId;
}
