package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.exception.EmailAlreadyExistsException;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthExistenceValidator {

    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private ConfirmationDomainService confirmationDomainService;

    public AuthExistenceValidator validateUsername(String username) {
        if (userAuthDomainService.existsByUsername(username)
                || confirmationDomainService.existsByNewUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }
        return this;
    }

    public AuthExistenceValidator validateEmail(String email) {
        if (userAuthDomainService.existsByEmail(email)
                || userAuthDomainService.existsByGoogleAuth(email)
                || confirmationDomainService.existsByNewEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        return this;
    }
}
