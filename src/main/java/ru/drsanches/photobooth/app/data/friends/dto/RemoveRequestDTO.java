package ru.drsanches.photobooth.app.data.friends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.service.validation.annotation.ExistsId;
import ru.drsanches.photobooth.app.service.validation.annotation.NotCurrentId;

public class RemoveRequestDTO {

    @NotEmpty
    @ExistsId
    @NotCurrentId
    @Schema(required = true)
    private String userId;

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "RemoveRequestDTO{" +
                "userId='" + userId + '\'' +
                '}';
    }
}