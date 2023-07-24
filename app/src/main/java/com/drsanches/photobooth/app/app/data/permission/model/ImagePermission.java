package com.drsanches.photobooth.app.app.data.permission.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name="image_permissions", indexes = @Index(name = "image_permissions_user_id_index", columnList = "userId"))
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
