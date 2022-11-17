package ru.drsanches.photobooth.auth.service.utils.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.auth.data.confirmation.model.Operation;

import java.util.Map;

//TODO: Move content to resources
@Component
public class EmailNotifier {

    @Autowired
    private EmailService emailService;

    private final Map<Operation, String> CONFIRM_SUBJECTS = Map.of(
            Operation.REGISTRATION, "Verify email for registration",
            Operation.USERNAME_CHANGE, "Submit username change",
            Operation.PASSWORD_CHANGE, "Submit password change",
            Operation.EMAIL_CHANGE, "Submit email change",
            Operation.DISABLE, "Submit account deletion"
    );

    private final Map<Operation, String> SUCCESS_SUBJECTS = Map.of(
            Operation.REGISTRATION, "Registration completed successfully",
            Operation.USERNAME_CHANGE, "Username changed successfully",
            Operation.PASSWORD_CHANGE, "Password changed successfully",
            Operation.EMAIL_CHANGE, "Email changed successfully",
            Operation.DISABLE, "Account deleted successfully"
    );

    public void sendCode(String code, String email, Operation operation) {
        emailService.sendHtmlMessage(email, CONFIRM_SUBJECTS.get(operation), "Code: " + code);
    }

    public void sendSuccessNotification(String email, Operation operation) {
        emailService.sendHtmlMessage(email, SUCCESS_SUBJECTS.get(operation), SUCCESS_SUBJECTS.get(operation));
    }
}
