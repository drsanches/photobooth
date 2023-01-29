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
public class ChangePasswordConfirmData {

    private String encryptedPassword;

    private String salt;
}
