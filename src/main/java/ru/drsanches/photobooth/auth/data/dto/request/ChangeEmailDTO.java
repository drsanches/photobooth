package ru.drsanches.photobooth.auth.data.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class ChangeEmailDTO {

    @Schema(maxLength = 255)
    @Length(max = 255)
    private String newEmail;
}
