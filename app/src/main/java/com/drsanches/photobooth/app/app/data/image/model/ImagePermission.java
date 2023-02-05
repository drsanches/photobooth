package com.drsanches.photobooth.app.app.data.image.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name="image_permissions")
public class ImagePermission {

    @EmbeddedId
    private ImagePermissionKey id;

    public ImagePermission(String imageId, String userId) {
        this.id = new ImagePermissionKey(imageId, userId);
    }

    public String getImageId() {
        return id.getImageId();
    }

    public String getUserId() {
        return id.getUserId();
    }
}
