package com.drsanches.photobooth.app.app.dto.profile.request;

import com.drsanches.photobooth.app.app.validation.annotation.Name;
import com.drsanches.photobooth.app.app.validation.annotation.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserProfileDto {

    @Schema(maxLength = 100, pattern = Name.PATTERN)
    @Length(max = 100)
    @Name
    private String name;

    @Schema(maxLength = 50, pattern = Status.PATTERN)
    @Length(max = 50)
    @Status
    private String status;
}
