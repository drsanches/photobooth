package com.drsanches.photobooth.app.notifier.service.integration;

import com.drsanches.photobooth.app.common.integration.notifier.NotifierIntegrationService;
import com.drsanches.photobooth.app.notifier.data.email.EmailInfoDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierIntegrationServiceImpl implements NotifierIntegrationService {

    @Autowired
    private EmailInfoDomainService emailInfoDomainService;

    //TODO: Refactor
    @Override
    public void updateEmail(String userId, String email) {
        emailInfoDomainService.deleteByUserId(userId);
        emailInfoDomainService.create(userId, email);
    }

    @Override
    public void removeEmail(String userId) {
        emailInfoDomainService.deleteByUserId(userId);
    }
}
