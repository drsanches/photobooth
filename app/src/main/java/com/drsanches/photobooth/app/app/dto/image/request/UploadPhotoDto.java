package com.drsanches.photobooth.app.app.dto.image.request;

import com.drsanches.photobooth.app.app.validation.annotation.EnabledIds;
import com.drsanches.photobooth.app.app.validation.annotation.FriendIds;
import com.drsanches.photobooth.app.app.validation.annotation.ValidBase64Image;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class UploadPhotoDto {

    @Schema(description = "Image in Base64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @ValidBase64Image
    @ToString.Exclude
    private String file;

    @Schema(description = "Ids of users to whom the photo is being sent, null for all friends")
    @EnabledIds
    @FriendIds
    private List<String> userIds;

    @ToString.Include
    private int base64length() {
        return file.length();
    }
}
