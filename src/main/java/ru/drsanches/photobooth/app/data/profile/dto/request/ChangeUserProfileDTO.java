package ru.drsanches.photobooth.app.data.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class ChangeUserProfileDTO {

    @Schema(maxLength = 100)
    @Length(max = 100)
    private String name;

    @Schema(maxLength = 50)
    @Length(max = 50)
    private String status;
}
