package com.drsanches.photobooth.app.auth.utils.email;

import com.drsanches.photobooth.app.notifier.service.notifier.email.service.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    private MimeMessage mimeMessage;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendHtmlMessage() throws MessagingException, IOException {
        var to = UUID.randomUUID().toString();
        var subject = UUID.randomUUID().toString();
        var message = UUID.randomUUID().toString();
        mimeMessage = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlMessage(to, subject, message);

        verify(emailSender).send(any(MimeMessage.class));
        assertEquals(to, mimeMessage.getHeader("To")[0]);
        assertEquals(subject, mimeMessage.getSubject());
        assertEquals(message, extractMessage(mimeMessage));
    }

    @Test
    void sendHtmlMessageForTestEmail() {
        var to = UUID.randomUUID() + "@example.com";
        var subject = UUID.randomUUID().toString();
        var message = UUID.randomUUID().toString();

        emailService.sendHtmlMessage(to, subject, message);

        verifyNoInteractions(emailSender);
    }

    private String extractMessage(MimeMessage mimeMessage) throws IOException, MessagingException {
        return (String) ((MimeMultipart) ((MimeMultipart) mimeMessage.getContent())
                .getBodyPart(0).getContent())
                .getBodyPart(0).getContent();
    }
}
