package com.drsanches.photobooth.app.app.data.permission.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="image_permissions", indexes = {
        @Index(name = "image_permissions_user_id_index", columnList = "userId")
})
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
