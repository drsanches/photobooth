package com.drsanches.photobooth.app.auth.data.google.dto;

import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleGetTokenDTO {

    @Schema(required = true)
    private TokenDTO token;

    @Schema(description = "Confirmation code from registration request")
    private String changeUsernameCode;
}
