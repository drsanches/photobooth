package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenDto {

    @Schema(description = "Google OAuth id token", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @ToString.Exclude
    private String idToken;
}
