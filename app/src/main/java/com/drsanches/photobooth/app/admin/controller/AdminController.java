package com.drsanches.photobooth.app.admin.controller;

import com.drsanches.photobooth.app.admin.dto.CreateTestUserDto;
import com.drsanches.photobooth.app.admin.service.AdminWebService;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.integration.auth.UserInfoDto;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class AdminController {

    @Autowired
    private AdminWebService adminWebService;

    @Operation(summary = "Creates new test user")
    @ApiTokenAuthorization //TODO: Also must be an admin
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/test/user", method = RequestMethod.POST)
    public UserInfoDto createUser(@RequestBody CreateTestUserDto createTestUserDto) {
        return adminWebService.createUser(createTestUserDto);
    }
}
