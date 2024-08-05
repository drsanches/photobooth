package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.service.GoogleAuthWebService;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/google", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class GoogleAuthController {

    @Autowired
    private GoogleAuthWebService googleAuthWebService;

    @Operation(summary = "Creates a new account or link to the existent")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public GoogleGetTokenDto getToken(@RequestBody GoogleTokenDto googleTokenDto) {
        return googleAuthWebService.getToken(googleTokenDto);
    }

    @Operation(summary = "Sets new username after account creation")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/username", method = RequestMethod.POST)
    public void setUsername(@RequestBody GoogleSetUsernameDto googleSetUsernameDto) {
        googleAuthWebService.setUsername(googleSetUsernameDto);
    }

    @Operation(summary = "Links with google account")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public void link(@RequestBody GoogleTokenDto googleTokenDto) {
        googleAuthWebService.link(googleTokenDto);
    }

    @Operation(summary = "Removes link with google account")
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/link", method = RequestMethod.DELETE)
    public void unlink() {
        googleAuthWebService.unlink();
    }
}
