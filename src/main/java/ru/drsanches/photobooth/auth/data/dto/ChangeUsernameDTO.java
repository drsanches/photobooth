package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ChangeUsernameDTO {

    @NotEmpty
    @Schema(required = true)
    private String newUsername;

    @NotEmpty
    @ToString.Exclude
    @Schema(required = true, description = "current user password SHA256 hash")
    private String password;
}
