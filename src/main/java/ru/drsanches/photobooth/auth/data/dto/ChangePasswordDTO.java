package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;

public class ChangePasswordDTO {

    @NotEmpty
    @Schema(required = true, description = "current user password hash")
    private String oldPassword;

    @NotEmpty
    @Schema(required = true, description = "new password hash")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordDTO{}";
    }
}