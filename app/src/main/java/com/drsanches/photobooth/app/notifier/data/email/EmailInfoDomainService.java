package com.drsanches.photobooth.app.notifier.data.email;

import com.drsanches.photobooth.app.common.exception.server.ServerError;
import com.drsanches.photobooth.app.notifier.data.email.model.EmailInfo;
import com.drsanches.photobooth.app.notifier.data.email.repository.EmailInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailInfoDomainService {

    @Autowired
    private EmailInfoRepository emailInfoRepository;

    public EmailInfo getByUserId(String userId) {
        return emailInfoRepository.findByIdUserId(userId).orElseThrow(() -> new ServerError("Email not found"));
    }
}
