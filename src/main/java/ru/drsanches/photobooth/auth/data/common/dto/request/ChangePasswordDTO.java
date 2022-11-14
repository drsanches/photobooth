package ru.drsanches.photobooth.auth.data.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ChangePasswordDTO {

    @Schema(required = true, maxLength = 255, description = "new password SHA256 hash")
    @NotEmpty
    @Length(max = 255)
    @ToString.Exclude
    private String newPassword;
}
