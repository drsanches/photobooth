package com.drsanches.photobooth.app.notifier.service.notifier.email.service;

public interface EmailService {

    void sendHtmlMessage(String to, String subject, String message);
}
