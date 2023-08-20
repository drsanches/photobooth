package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationCodeDto {

    @Schema(description = "Confirmation code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String code;
}
