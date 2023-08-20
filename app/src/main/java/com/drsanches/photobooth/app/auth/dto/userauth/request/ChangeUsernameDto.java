package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import com.drsanches.photobooth.app.auth.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangeUsernameDto {

    @Schema(maxLength = 20, pattern = Username.PATTERN, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    @Username
    @NonexistentUsername
    private String newUsername;
}
