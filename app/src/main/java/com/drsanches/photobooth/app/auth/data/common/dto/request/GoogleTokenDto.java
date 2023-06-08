package com.drsanches.photobooth.app.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenDto {

    @Schema(required = true, description = "Google OAuth id token")
    @NotEmpty
    @ToString.Exclude
    private String idToken;
}
