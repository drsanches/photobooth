package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.common.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsernameDto {

    @Schema(maxLength = 20, pattern = Username.PATTERN, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    @Username
    private String newUsername;
}
