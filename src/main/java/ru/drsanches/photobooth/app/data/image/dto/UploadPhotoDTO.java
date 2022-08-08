package ru.drsanches.photobooth.app.data.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.service.validation.annotation.EnabledIds;
import ru.drsanches.photobooth.app.service.validation.annotation.FriendIds;
import ru.drsanches.photobooth.app.service.validation.annotation.ValidBase64Image;

import java.util.List;

@Getter
@Setter
@ToString
public class UploadPhotoDTO {

    @NotEmpty
    @ValidBase64Image
    @ToString.Exclude
    @Schema(required = true, description = "Image in Base64")
    private String file;

    @NotEmpty
    @EnabledIds
    @FriendIds
    @Schema(required = true, description = "Ids of users to whom the photo is being sent")
    private List<String> userIds;

    @ToString.Include
    private int base64length() {
        return file.length();
    }
}
