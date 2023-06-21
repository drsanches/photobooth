package com.drsanches.photobooth.app.auth.utils.email;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.config.EmailNotificationsContentProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EmailNotifierTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EmailNotificationsContentProperties emailNotificationsContentProperties;

    @InjectMocks
    private EmailNotifier emailNotifier;

    @Test
    void sendCode() {
        Operation operation = Operation.REGISTRATION;
        String code = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();
        String subject = UUID.randomUUID().toString();
        String text = UUID.randomUUID() + "%s" + UUID.randomUUID();
        Mockito.when(emailNotificationsContentProperties.getConfirmSubject(operation)).thenReturn(subject);
        Mockito.when(emailNotificationsContentProperties.getConfirmText(operation)).thenReturn(text);

        emailNotifier.sendCode(code, email, operation);

        Mockito.verify(emailService, Mockito.times(1)).sendHtmlMessage(email, subject, String.format(text, code));
    }

    @Test
    void sendSuccessNotification() {
        Operation operation = Operation.REGISTRATION;
        String email = UUID.randomUUID().toString();
        String subject = UUID.randomUUID().toString();
        String text = UUID.randomUUID().toString();
        Mockito.when(emailNotificationsContentProperties.getSuccessSubject(operation)).thenReturn(subject);
        Mockito.when(emailNotificationsContentProperties.getSuccessText(operation)).thenReturn(text);

        emailNotifier.sendSuccessNotification(email, operation);

        Mockito.verify(emailService, Mockito.times(1)).sendHtmlMessage(email, subject, text);
    }
}
