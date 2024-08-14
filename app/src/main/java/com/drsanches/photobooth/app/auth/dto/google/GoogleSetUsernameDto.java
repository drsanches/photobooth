package com.drsanches.photobooth.app.auth.dto.google;

import com.drsanches.photobooth.app.auth.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleSetUsernameDto {

    @Schema(maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    @Username
    private String newUsername;

    @Schema(description = "Confirmation code from registration request", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String code;
}
