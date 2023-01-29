package com.drsanches.photobooth.app.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class GoogleTokenDTO {

    @Schema(required = true, description = "Google OAuth id token")
    @NotEmpty
    @ToString.Exclude
    private String idToken;
}
