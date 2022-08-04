package ru.drsanches.photobooth.app.data.friends.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.photobooth.app.service.validation.annotation.EnabledId;
import ru.drsanches.photobooth.app.service.validation.annotation.NotCurrentId;

public class SendRequestDTO {

    @NotEmpty
    @EnabledId
    @NotCurrentId
    @Schema(required = true)
    private String userId;

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "SendRequestDTO{" +
                "userId='" + userId + '\'' +
                '}';
    }
}