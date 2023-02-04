package com.drsanches.photobooth.app.auth.service.utils.email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    private MimeMessage mimeMessage;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage((Session) null);
        Mockito.when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendHtmlMessage() throws MessagingException, IOException {
        String to = UUID.randomUUID().toString();
        String subject = UUID.randomUUID().toString();
        String message = UUID.randomUUID().toString();

        emailService.sendHtmlMessage(to, subject, message);
        Mockito.verify(emailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
        Assertions.assertEquals(to, mimeMessage.getHeader("To")[0]);
        Assertions.assertEquals(subject, mimeMessage.getSubject());
        Assertions.assertEquals(message, extractMessage(mimeMessage));
    }

    private String extractMessage(MimeMessage mimeMessage) throws IOException, MessagingException {
        return (String) ((MimeMultipart) ((MimeMultipart) mimeMessage.getContent())
                .getBodyPart(0).getContent())
                .getBodyPart(0).getContent();
    }
}