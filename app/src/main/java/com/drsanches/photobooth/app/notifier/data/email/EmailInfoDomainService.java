package com.drsanches.photobooth.app.notifier.data.email;

import com.drsanches.photobooth.app.notifier.data.email.model.EmailInfo;
import com.drsanches.photobooth.app.notifier.data.email.repository.EmailInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmailInfoDomainService {

    @Autowired
    private EmailInfoRepository emailInfoRepository;

    public EmailInfo create(String userId, String email) {
        var emailInfo = emailInfoRepository.save(new EmailInfo(userId, email));
        log.info("New EmailInfo saved: " + emailInfo);
        return emailInfo;
    }

    public Optional<EmailInfo> findByUserId(String userId) {
        return emailInfoRepository.findByIdUserId(userId);
    }

    public void deleteByUserId(String userId) {
        emailInfoRepository.findByIdUserId(userId).ifPresent(it -> {
            emailInfoRepository.delete(it);
            log.info("EmailInfo deleted: " + it);
        });
    }
}
