package ru.drsanches.photobooth.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.auth.validation.annotation.NonexistentUsername;

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
    private String email;
}
