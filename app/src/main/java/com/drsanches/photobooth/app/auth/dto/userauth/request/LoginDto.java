package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginDto {

    @Schema(maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    private String username;

    @Schema(description = "password SHA256 hash", maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String password;
}
