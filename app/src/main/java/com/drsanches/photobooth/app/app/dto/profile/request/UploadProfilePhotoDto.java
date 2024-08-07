package com.drsanches.photobooth.app.app.dto.profile.request;

import com.drsanches.photobooth.app.app.validation.annotation.ValidBase64Image;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
public class UploadProfilePhotoDto {

    @Schema(description = "Image in Base64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @ValidBase64Image
    @ToString.Exclude
    private String imageData;

    @ToString.Include
    private int base64length() {
        return imageData.length();
    }
}
