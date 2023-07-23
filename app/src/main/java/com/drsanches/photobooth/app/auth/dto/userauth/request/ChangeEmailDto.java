package com.drsanches.photobooth.app.auth.dto.userauth.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class ChangeEmailDto {

    @Schema(maxLength = 255)
    @NotEmpty
    @Email
    @NonexistentEmail
    private String newEmail;
}
