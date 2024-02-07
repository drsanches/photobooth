package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.service.AuthIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthIntegrationServiceImpl implements AuthIntegrationService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Override
    public String getUsername(String userId) {
        return userAuthDomainService.getEnabledById(userId).getUsername();
    }

    @Override
    public String getEmail(String userId) {
        return userAuthDomainService.getEnabledById(userId).getEmail();
    }
}
