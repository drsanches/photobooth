package com.drsanches.photobooth.app.app.data.friends.dto.request;

import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import com.drsanches.photobooth.app.app.validation.annotation.EnabledId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class SendRequestDto {

    @Schema(required = true)
    @NotEmpty
    @EnabledId
    @NotCurrentId
    private String userId;
}
