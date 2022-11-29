package ru.drsanches.photobooth.app.data.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.validation.annotation.ValidBase64Image;

@Getter
@Setter
@ToString
public class UploadAvatarDTO {

    @Schema(required = true, description = "Image in Base64")
    @NotEmpty
    @ValidBase64Image
    @ToString.Exclude
    private String image;

    @ToString.Include
    private int base64length() {
        return image.length();
    }
}
