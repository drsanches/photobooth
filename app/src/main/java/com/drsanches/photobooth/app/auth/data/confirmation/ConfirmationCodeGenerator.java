package com.drsanches.photobooth.app.auth.data.confirmation;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationCodeGenerator {

    public String generate() {
        return RandomStringUtils.randomAlphanumeric(6);
    }
}
