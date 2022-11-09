package ru.drsanches.photobooth.app.data.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.validation.annotation.EnabledIds;
import ru.drsanches.photobooth.app.validation.annotation.FriendIds;
import ru.drsanches.photobooth.app.validation.annotation.ValidBase64Image;

import java.util.List;

@Getter
@Setter
@ToString
public class UploadPhotoDTO {

    //TODO: Rename to image
    @Schema(required = true, description = "Image in Base64")
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
