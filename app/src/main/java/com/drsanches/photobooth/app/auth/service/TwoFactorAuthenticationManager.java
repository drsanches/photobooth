package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwoFactorAuthenticationManager {

    @Value("${application.2fa.actions}")
    private List<Operation> actions;
    @Value("${application.address}")
    private String host;

    public boolean isEnabled(Operation operation) {
        return actions.contains(operation);
    }

    public String getConfirmationLink(String code) {
        return host + "/api/v1/auth/confirm/" + code;
    }
}
