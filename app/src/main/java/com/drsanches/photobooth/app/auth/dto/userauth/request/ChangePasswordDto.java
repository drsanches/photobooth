package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

    @Schema(description = "new password SHA256 hash", maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String newPassword;
}
