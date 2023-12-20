package com.drsanches.photobooth.app.notifier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenExpiresDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String expires;
}
