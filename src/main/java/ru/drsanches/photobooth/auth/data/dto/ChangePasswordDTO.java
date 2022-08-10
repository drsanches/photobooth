package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ChangePasswordDTO {

    @NotEmpty
    @ToString.Exclude
    @Schema(required = true, description = "current user password SHA256 hash")
    private String oldPassword;

    @NotEmpty
    @ToString.Exclude
    @Schema(required = true, description = "new password SHA256 hash")
    private String newPassword;
}
