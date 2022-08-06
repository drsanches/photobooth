package ru.drsanches.photobooth.app.data.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeUserProfileDTO {

    @Schema
    private String firstName;

    @Schema
    private String lastName;
}