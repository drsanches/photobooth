package com.drsanches.photobooth.app.app.data.permission.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
