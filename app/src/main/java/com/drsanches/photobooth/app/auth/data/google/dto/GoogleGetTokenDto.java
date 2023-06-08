package com.drsanches.photobooth.app.auth.data.google.dto;

import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleGetTokenDto {

    @Schema(required = true)
    private TokenDto token;

    @Schema(description = "Confirmation code from registration request")
    private String changeUsernameCode;
}
