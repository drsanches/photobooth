package ru.drsanches.photobooth.app.data.friends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.service.validation.annotation.EnabledId;
import ru.drsanches.photobooth.app.service.validation.annotation.NotCurrentId;

@Getter
@Setter
@ToString
public class SendRequestDTO {

    @NotEmpty
    @EnabledId
    @NotCurrentId
    @Schema(required = true)
    private String userId;
}