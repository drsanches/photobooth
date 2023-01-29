package com.drsanches.photobooth.app.auth.data.common.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrationConfirmData {

    private String username;

    private String email;

    @ToString.Exclude
    private String encryptedPassword;

    @ToString.Exclude
    private String salt;
}
