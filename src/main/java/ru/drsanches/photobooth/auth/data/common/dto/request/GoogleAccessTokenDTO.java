package ru.drsanches.photobooth.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class GoogleAccessTokenDTO {

    @Schema(required = true, description = "Google OAuth access token")
    @NotEmpty
    @Length(max = 300) //TODO: determine the length
    @Pattern(regexp = "ya29\\.[a-zA-Z0-9\\-_]*", message = "wrong google token format")
    @ToString.Exclude
    private String accessToken;
}
