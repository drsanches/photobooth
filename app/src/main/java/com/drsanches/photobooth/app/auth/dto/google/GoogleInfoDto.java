package com.drsanches.photobooth.app.auth.dto.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleInfoDto {

    private String email;

    private Boolean email_verified;

    private String name;

    private String picture;

    private String given_name;

    private String family_name;

    private String locale;
}
