package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;

public class DeleteUserDTO {

    @NotEmpty
    @Schema(required = true, description = "current user password hash")
    private String password;

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DeleteUserDTO{}";
    }
}