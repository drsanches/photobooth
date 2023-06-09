package com.drsanches.photobooth.app.app.dto.profile.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangeUserProfileDto {

    @Schema(maxLength = 100)
    @Length(max = 100)
    private String name;

    @Schema(maxLength = 50)
    @Length(max = 50)
    private String status;
}
