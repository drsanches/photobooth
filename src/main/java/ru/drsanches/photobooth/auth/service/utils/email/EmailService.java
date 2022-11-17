package ru.drsanches.photobooth.auth.service.utils.email;

public interface EmailService {

    void sendHtmlMessage(String to, String subject, String message);
}
