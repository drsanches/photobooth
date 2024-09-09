package com.drsanches.photobooth.app.admin.service;

import com.drsanches.photobooth.app.admin.dto.CreateTestUserDto;
import com.drsanches.photobooth.app.common.integration.app.AppIntegrationService;
import com.drsanches.photobooth.app.common.integration.auth.AuthIntegrationService;
import com.drsanches.photobooth.app.common.integration.auth.UserInfoDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Slf4j
@Service
@Validated
public class AdminWebService {

    @Autowired
    private AuthIntegrationService authIntegrationService;
    @Autowired
    private AppIntegrationService appIntegrationService;

    public UserInfoDto createUser(@Valid CreateTestUserDto createTestUserDto) {
        var user = authIntegrationService.createAccount(
                createTestUserDto.getUsername(),
                UUID.randomUUID() + "@example.com",
                createTestUserDto.getPassword()
        );
        appIntegrationService.createProfile(user.id(), user.username());
        return user;
    }
}
