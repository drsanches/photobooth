package com.drsanches.photobooth.app.admin.service;

import com.drsanches.photobooth.app.admin.dto.CreateTestUserDto;
import com.drsanches.photobooth.app.common.integration.app.AppIntegrationService;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.integration.auth.UserCreationInfoDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class AdminWebService {

    @Autowired
    private AuthIntegrationService authIntegrationService;
    @Autowired
    private AppIntegrationService appIntegrationService;

    public UserCreationInfoDto createUser(@Valid CreateTestUserDto createTestUserDto) {
        var user = authIntegrationService.createAccount(
                createTestUserDto.getUsername(),
                createTestUserDto.getEmail(),
                createTestUserDto.getPassword()
        );
        appIntegrationService.createProfile(user.id(), user.username());
        log.info("User was created by admin. UserInfo: {}", user);
        return user;
    }
}
