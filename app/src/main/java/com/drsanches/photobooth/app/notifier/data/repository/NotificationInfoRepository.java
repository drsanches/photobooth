package com.drsanches.photobooth.app.notifier.data.repository;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.notifier.data.model.NotificationInfo;
import com.drsanches.photobooth.app.notifier.data.model.NotificationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface NotificationInfoRepository extends CrudRepository<NotificationInfo, String> {

    @Override
    @NonNull
    <S extends NotificationInfo> S save(@NonNull S entity);

    @Override
    @NonNull
    Optional<NotificationInfo> findById(@NonNull String s);

    List<NotificationInfo> findByUserIdAndType(String userId, NotificationType type);

    void deleteByUserIdAndType(String userId, NotificationType type);
}
