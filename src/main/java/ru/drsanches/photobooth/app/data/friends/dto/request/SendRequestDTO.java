package ru.drsanches.photobooth.app.data.friends.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.validation.annotation.EnabledId;
import ru.drsanches.photobooth.app.validation.annotation.NotCurrentId;

@Getter
@Setter
@ToString
public class SendRequestDTO {

    @Schema(required = true)
    @NotEmpty
    @EnabledId
    @NotCurrentId
    private String userId;
}
