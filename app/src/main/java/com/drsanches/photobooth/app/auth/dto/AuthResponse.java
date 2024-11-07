package com.drsanches.photobooth.app.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthResponse<T> {

    @Schema(description = "Result of operation, can be empty. Exists if with2FA is false")
    private T result;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean with2FA;

    public AuthResponse(T result, boolean with2FA) {
        this.result = result;
        this.with2FA = with2FA;
    }

    public AuthResponse(boolean with2FA) {
        this.with2FA = with2FA;
    }
}
