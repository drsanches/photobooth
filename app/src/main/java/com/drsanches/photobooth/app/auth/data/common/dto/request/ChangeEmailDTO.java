package com.drsanches.photobooth.app.auth.data.common.dto.request;

import com.drsanches.photobooth.app.auth.validation.annotation.NonexistentEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ChangeEmailDTO {

    @Schema(maxLength = 255)
    @NotEmpty
    @Length(max = 255)
    @NonexistentEmail
    private String newEmail;
}
