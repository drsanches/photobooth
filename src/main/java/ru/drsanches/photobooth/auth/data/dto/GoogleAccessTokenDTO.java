package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.auth.validation.annotation.GoogleAccessToken;

@Getter
@Setter
@ToString
public class GoogleAccessTokenDTO {

    @GoogleAccessToken
    @NotEmpty
    @ToString.Exclude
    @Schema(required = true, description = "Google OAuth access token")
    private String accessToken;
}
