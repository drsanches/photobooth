package com.drsanches.photobooth.app.auth.data.common.dto.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class RegistrationDTO {

    @Schema(required = true, maxLength = 20)
    @NotEmpty
    @Length(max = 20)
    @NonexistentUsername
    private String username;

    @Schema(required = true, maxLength = 255, description = "password SHA256 hash")
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String password;

    @Schema(maxLength = 255)
    @NotEmpty
    @Length(max = 255)
    @NonexistentEmail
    private String email;
}
