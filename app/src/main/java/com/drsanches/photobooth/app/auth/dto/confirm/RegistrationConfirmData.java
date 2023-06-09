package com.drsanches.photobooth.app.auth.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationConfirmData {

    private String username;

    private String email;

    @ToString.Exclude
    private String encryptedPassword;

    @ToString.Exclude
    private String salt;
}
