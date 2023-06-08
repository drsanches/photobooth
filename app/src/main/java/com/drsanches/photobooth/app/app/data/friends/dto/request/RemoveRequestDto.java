package com.drsanches.photobooth.app.app.data.friends.dto.request;

import com.drsanches.photobooth.app.app.validation.annotation.ExistsId;
import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class RemoveRequestDto {

    @Schema(required = true)
    @NotEmpty
    @ExistsId
    @NotCurrentId
    private String userId;
}
