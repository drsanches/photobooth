package ru.drsanches.photobooth.app.data.friends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.service.validation.annotation.ExistsId;
import ru.drsanches.photobooth.app.service.validation.annotation.NotCurrentId;

@Getter
@Setter
@ToString
public class RemoveRequestDTO {

    @NotEmpty
    @ExistsId
    @NotCurrentId
    @Schema(required = true)
    private String userId;
}
