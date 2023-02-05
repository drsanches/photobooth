package com.drsanches.photobooth.app.app.data.image.dto.request;

import com.drsanches.photobooth.app.app.validation.annotation.ValidBase64Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class UploadAvatarDTO {

    @Schema(required = true, description = "Image in Base64")
    @NotEmpty
    @ValidBase64Image
    @ToString.Exclude
    private String file;

    @ToString.Include
    private int base64length() {
        return file.length();
    }
}
