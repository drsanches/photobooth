package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailDto {

    //TODO: Limit length?
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Email
    private String newEmail;
}
