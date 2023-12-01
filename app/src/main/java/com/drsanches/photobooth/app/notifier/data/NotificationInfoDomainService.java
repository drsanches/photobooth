package com.drsanches.photobooth.app.notifier.data;

import com.drsanches.photobooth.app.notifier.data.model.NotificationInfo;
import com.drsanches.photobooth.app.notifier.data.model.NotificationType;
import com.drsanches.photobooth.app.notifier.data.repository.NotificationInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationInfoDomainService {

    @Autowired
    private NotificationInfoRepository notificationInfoRepository;

    public List<NotificationInfo> getByUserIdAndType(String userId, NotificationType type) {
        return notificationInfoRepository.findByUserIdAndType(userId, type);
    }
}
