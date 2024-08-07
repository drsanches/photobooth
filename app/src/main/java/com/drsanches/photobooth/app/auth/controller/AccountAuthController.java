package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.dto.AuthResponse;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.CreateAccountDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.service.AccountAuthWebService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdatePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/account", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class AccountAuthController {

    @Autowired
    private AccountAuthWebService accountAuthWebService;

    @Operation(summary = "Creates new account (2FA available)")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(value = "", method = RequestMethod.POST)
    public AuthResponse<TokenDto> createAccount(@RequestBody CreateAccountDto createAccountDto) {
        return accountAuthWebService.createAccount(createAccountDto);
    }

    @Operation(summary = "Returns current account private information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.GET)
    public UserAuthInfoDto getAccount() {
        return accountAuthWebService.getAccount();
    }

    @Operation(summary = "Updates username (2FA available)")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/username", method = RequestMethod.POST)
    public AuthResponse<Void> updateUsername(@RequestBody UpdateUsernameDto updateUsernameDto) {
        return accountAuthWebService.updateUsername(updateUsernameDto);
    }

    @Operation(summary = "Updates password (2FA available)")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public AuthResponse<Void> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        return accountAuthWebService.updatePassword(updatePasswordDto);
    }

    @Operation(summary = "Updates email (2FA available)")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public AuthResponse<Void> updateEmail(@RequestBody UpdateEmailDto updateEmailDto) {
        return accountAuthWebService.updateEmail(updateEmailDto);
    }

    @Operation(summary = "Disables current user account (2FA available)")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public AuthResponse<Void> disableUser() {
        return accountAuthWebService.disableUser();
    }

    @Operation(summary = "Completes 2FA operation")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/confirm/{confirmationCode}", method = RequestMethod.GET)
    public void confirm(@PathVariable String confirmationCode) {
        accountAuthWebService.confirm(confirmationCode);
    }
}
