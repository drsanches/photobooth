package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserAuthInfoDTO {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String username;

    @Schema(required = true)
    private String email;
}