package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import com.drsanches.photobooth.app.auth.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class RegistrationDto {

    @Schema(required = true, maxLength = 20)
    @NotEmpty
    @Length(max = 20)
    @Username
    @NonexistentUsername
    private String username;

    @Schema(required = true, maxLength = 255, description = "password SHA256 hash")
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
