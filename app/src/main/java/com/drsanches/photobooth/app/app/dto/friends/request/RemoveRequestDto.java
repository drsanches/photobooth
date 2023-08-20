package com.drsanches.photobooth.app.app.dto.friends.request;

import com.drsanches.photobooth.app.app.validation.annotation.ExistsId;
import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RemoveRequestDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @ExistsId
    @NotCurrentId
    private String userId;
}
