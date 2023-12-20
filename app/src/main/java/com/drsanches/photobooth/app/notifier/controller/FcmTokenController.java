package com.drsanches.photobooth.app.notifier.controller;

import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import com.drsanches.photobooth.app.notifier.service.web.FcmTokenWebService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/notification/fcm/token", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class FcmTokenController {

    @Autowired
    private FcmTokenWebService fcmTokenWebService;

    @Operation(summary = "Adds new fcm token")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.POST)
    public FcmTokenExpiresDto addToken(@RequestBody FcmTokenDto fcmTokenDto) {
        return fcmTokenWebService.addToken(fcmTokenDto);
    }
}
