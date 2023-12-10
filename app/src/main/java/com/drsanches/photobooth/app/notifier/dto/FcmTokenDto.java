package com.drsanches.photobooth.app.notifier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @ToString.Exclude
    String fcmToken;
}
