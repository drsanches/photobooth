package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;

public class LoginDTO {

    @NotEmpty
    @Schema(required = true)
    private String username;

    @NotEmpty
    @Schema(required = true, description = "password hash")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}