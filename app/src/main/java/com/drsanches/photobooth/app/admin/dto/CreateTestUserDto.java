package com.drsanches.photobooth.app.admin.dto;

import com.drsanches.photobooth.app.common.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestUserDto {

    @Schema(maxLength = 20, pattern = Username.PATTERN, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 20)
    @Username
    private String username;

    @Schema(maxLength = 255, description = "password SHA256 hash", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String password;

    @Schema(maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    //TODO: Length?
    @NotEmpty
    @Email
    private String email;
}
