package com.drsanches.photobooth.app.auth.data.confirmation;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConfirmationCodeGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
