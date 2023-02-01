package com.drsanches.photobooth.app.auth.service.utils.email;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EmailNotifierTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotifier emailNotifier;

    @ParameterizedTest
    @MethodSource("sendCodeData")
    void sendCode(Operation operation, String subject) {
        String code = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();
        emailNotifier.sendCode(code, email, operation);
        Mockito.verify(emailService, Mockito.times(1)).sendHtmlMessage(email, subject, "Code: " + code);
    }

    static Arguments[] sendCodeData() {
        return new Arguments[] {
                Arguments.of(Operation.REGISTRATION, "Verify email for registration"),
                Arguments.of(Operation.USERNAME_CHANGE, "Submit username change"),
                Arguments.of(Operation.PASSWORD_CHANGE, "Submit password change"),
                Arguments.of(Operation.EMAIL_CHANGE, "Submit email change"),
                Arguments.of(Operation.DISABLE, "Submit account deletion")
        };
    }

    @ParameterizedTest
    @MethodSource("sendSuccessNotificationData")
    void sendSuccessNotification(Operation operation, String subject, String message) {
        String email = UUID.randomUUID().toString();
        emailNotifier.sendSuccessNotification(email, operation);
        Mockito.verify(emailService, Mockito.times(1)).sendHtmlMessage(email, subject, message);
    }

    static Arguments[] sendSuccessNotificationData() {
        return new Arguments[] {
                Arguments.of(Operation.REGISTRATION, "Registration completed successfully", "Registration completed successfully"),
                Arguments.of(Operation.USERNAME_CHANGE, "Username changed successfully", "Username changed successfully"),
                Arguments.of(Operation.PASSWORD_CHANGE, "Password changed successfully", "Password changed successfully"),
                Arguments.of(Operation.EMAIL_CHANGE, "Email changed successfully", "Email changed successfully"),
                Arguments.of(Operation.DISABLE, "Account deleted successfully", "Account deleted successfully")
        };
    }
}
