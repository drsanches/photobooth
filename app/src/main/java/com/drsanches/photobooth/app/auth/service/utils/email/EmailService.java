package com.drsanches.photobooth.app.auth.service.utils.email;

public interface EmailService {

    void sendHtmlMessage(String to, String subject, String message);
}
