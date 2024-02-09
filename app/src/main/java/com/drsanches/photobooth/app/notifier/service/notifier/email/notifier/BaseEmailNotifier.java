package com.drsanches.photobooth.app.notifier.service.notifier.email.notifier;

import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.notifier.data.email.EmailInfoDomainService;
import com.drsanches.photobooth.app.notifier.data.email.model.EmailInfo;
import com.drsanches.photobooth.app.notifier.service.notifier.Notifier;
import com.drsanches.photobooth.app.notifier.service.notifier.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseEmailNotifier implements Notifier {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailInfoDomainService emailInfoDomainService;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    protected void sendEmail(String email, String subject, String message) {
        emailService.sendHtmlMessage(email, subject, message);
    }

    protected String getEmail(String userId) {
        return emailInfoDomainService.findByUserId(userId)
                .map(EmailInfo::getEmail)
                .orElse(emailInfoDomainService.create(userId, authIntegrationService.getEmail(userId)).getEmail());
    }
}
