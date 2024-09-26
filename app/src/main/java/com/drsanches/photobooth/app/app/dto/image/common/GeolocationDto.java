package com.drsanches.photobooth.app.app.dto.image.common;

import com.drsanches.photobooth.app.app.validation.annotation.NullableTogether;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NullableTogether(fields = {"lat", "lng"}, action = NullableTogether.Action.SET_NULL)
public class GeolocationDto {

    @Schema(description = "Latitude")
    @Digits(integer = 2, fraction = 6)
    private BigDecimal lat;

    @Schema(description = "Longitude")
    @Digits(integer = 3, fraction = 6)
    private BigDecimal lng;
}
