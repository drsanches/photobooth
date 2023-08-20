package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangeEmailDto {

    //TODO: Limit length?
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Email
    @NonexistentEmail
    private String newEmail;
}
