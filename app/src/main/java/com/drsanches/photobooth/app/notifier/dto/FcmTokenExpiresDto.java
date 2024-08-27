package com.drsanches.photobooth.app.notifier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenExpiresDto {

    @Schema(pattern = "ISO-8601 (YYYY-MM-DDThh:mm:ss.sssZ)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expires;
}
