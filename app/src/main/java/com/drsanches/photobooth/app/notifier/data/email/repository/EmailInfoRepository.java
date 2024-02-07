package com.drsanches.photobooth.app.notifier.data.email.repository;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.notifier.data.email.model.EmailInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@MonitorTime
public interface EmailInfoRepository extends CrudRepository<EmailInfo, String> {

    @Override
    @NonNull
    <S extends EmailInfo> S save(@NonNull S entity);

    Optional<EmailInfo> findByIdUserId(String userId);

    @Override
    void delete(EmailInfo entity);
}
