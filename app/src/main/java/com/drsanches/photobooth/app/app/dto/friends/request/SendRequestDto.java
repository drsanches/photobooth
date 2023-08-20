package com.drsanches.photobooth.app.app.dto.friends.request;

import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import com.drsanches.photobooth.app.app.validation.annotation.EnabledId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendRequestDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @EnabledId
    @NotCurrentId
    private String userId;
}
