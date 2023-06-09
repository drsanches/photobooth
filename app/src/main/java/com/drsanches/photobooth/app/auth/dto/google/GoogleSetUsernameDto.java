package com.drsanches.photobooth.app.auth.dto.google;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class GoogleSetUsernameDto {

    @Schema(required = true, maxLength = 20)
    @NotEmpty
    @Length(max = 20)
    @NonexistentUsername
    private String newUsername;

    @Schema(required = true, description = "Confirmation code from registration request")
    @NotEmpty
    private String code;
}
