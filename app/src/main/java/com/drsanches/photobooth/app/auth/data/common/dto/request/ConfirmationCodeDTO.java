package com.drsanches.photobooth.app.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationCodeDTO {

    @Schema(required = true, description = "Confirmation code")
    @NotEmpty
    private String code;
}
