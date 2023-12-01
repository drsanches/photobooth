package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import com.drsanches.photobooth.app.auth.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    @Schema(maxLength = 20, pattern = Username.PATTERN, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    @Username
    @NonexistentUsername
    private String username;

    @Schema(maxLength = 255, description = "password SHA256 hash", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String password;

    @Schema(maxLength = 255)
    @NotEmpty
    @Email
    @NonexistentEmail
    private String email;
}
