package com.drsanches.photobooth.app.app.data.image.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class ImagePermissionKey implements Serializable {

    @Column(name = "imageId", nullable = false)
    private String imageId;

    @Column(name = "userId", nullable = false)
    private String userId;
}
