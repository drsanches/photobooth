package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.service.TokenAuthWebService;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/token", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class TokenAuthController {

    @Autowired
    private TokenAuthWebService tokenAuthWebService;

    @Operation(summary = "Returns authorization token")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.POST)
    public TokenDto getToken(@RequestBody LoginDto loginDto) {
        return tokenAuthWebService.getToken(loginDto);
    }

    @Operation(summary = "Returns authorization token")
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public TokenDto refreshToken(
            @RequestHeader("Authorization")
            @Parameter(description = "Refresh token", required = true)
            String refreshToken
    ) {
        return tokenAuthWebService.refreshToken(refreshToken);
    }

    @Operation(summary = "Removes current authorization token")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void logout() {
        tokenAuthWebService.removeToken();
    }
}
