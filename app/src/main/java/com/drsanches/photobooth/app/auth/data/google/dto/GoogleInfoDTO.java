package com.drsanches.photobooth.app.auth.data.google.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
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
