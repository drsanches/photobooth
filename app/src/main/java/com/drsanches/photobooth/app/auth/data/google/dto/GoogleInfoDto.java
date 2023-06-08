package com.drsanches.photobooth.app.auth.data.google.dto;

import lombok.Data;

@Data
public class GoogleInfoDto {

    private String email;

    private Boolean email_verified;

    private String name;

    private String picture;

    private String given_name;

    private String family_name;

    private String locale;
}