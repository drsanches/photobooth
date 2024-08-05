package com.drsanches.photobooth.app.auth.dto.google;

import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleGetTokenDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenDto token;

    @Schema(description = "Confirmation code to set username")
    private String changeUsernameCode;
}
