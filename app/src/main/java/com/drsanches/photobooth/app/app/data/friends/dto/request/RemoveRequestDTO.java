package com.drsanches.photobooth.app.app.data.friends.dto.request;

import com.drsanches.photobooth.app.app.validation.annotation.ExistsId;
import com.drsanches.photobooth.app.app.validation.annotation.NotCurrentId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class RemoveRequestDTO {

    @Schema(required = true)
    @NotEmpty
    @ExistsId
    @NotCurrentId
    private String userId;
}
