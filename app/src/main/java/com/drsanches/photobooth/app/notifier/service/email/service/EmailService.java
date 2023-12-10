package com.drsanches.photobooth.app.notifier.service.email.service;

public interface EmailService {

    void sendHtmlMessage(String to, String subject, String message);
}
