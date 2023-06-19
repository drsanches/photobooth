package com.drsanches.photobooth.app.auth.dto.userauth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationCodeDto {

    @Schema(required = true, description = "Confirmation code")
    @NotEmpty
    private String code;
}