package ru.drsanches.photobooth.app.data.friends.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.validation.annotation.ExistsId;
import ru.drsanches.photobooth.app.validation.annotation.NotCurrentId;

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
