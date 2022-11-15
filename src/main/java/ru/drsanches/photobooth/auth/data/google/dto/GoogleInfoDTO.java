package ru.drsanches.photobooth.auth.data.google.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GoogleInfoDTO {

    private String email;

    private Boolean email_verified;

    private String name;

    private String picture;

    private String given_name;

    private String family_name;

    private String locale;
}
