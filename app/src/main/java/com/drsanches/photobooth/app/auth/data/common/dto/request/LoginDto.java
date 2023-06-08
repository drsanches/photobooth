package com.drsanches.photobooth.app.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class LoginDto {

    @Schema(required = true, maxLength = 20)
    @NotEmpty
    @Length(max = 20)
    private String username;

    @Schema(required = true, maxLength = 255, description = "password SHA256 hash")
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String password;
}
