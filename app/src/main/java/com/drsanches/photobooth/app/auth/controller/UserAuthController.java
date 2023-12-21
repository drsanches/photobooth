package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.dto.AuthResponse;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.RegistrationDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.service.UserAuthWebService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class UserAuthController {

    @Autowired
    private UserAuthWebService userAuthWebService;

    @Operation(summary = "If 2FA is activated, registers the user, otherwise, sends a confirmation code to the mail")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public AuthResponse<TokenDto> registration(@RequestBody RegistrationDto registrationDto) {
        return userAuthWebService.registration(registrationDto);
    }

    @Operation(summary = "Returns authorization token")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public TokenDto login(@RequestBody LoginDto loginDto) {
        return userAuthWebService.login(loginDto);
    }

    @Operation(summary = "Returns current user private information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public UserAuthInfoDto info() {
        return userAuthWebService.info();
    }

    @Operation(summary = "Changes username. If 2FA is activated, sends a confirmation code to the email")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeUsername", method = RequestMethod.POST)
    public AuthResponse<Void> changeUsername(@RequestBody ChangeUsernameDto changeUsernameDto) {
        return userAuthWebService.changeUsername(changeUsernameDto);
    }

    @Operation(summary = "Changes password or sets new if does not exist. If 2FA is activated, sends a confirmation code to the email")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public AuthResponse<Void> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return userAuthWebService.changePassword(changePasswordDto);
    }

    @Operation(summary = "Changes email. If 2FA is activated, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
    public AuthResponse<Void> changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {
        return userAuthWebService.changeEmail(changeEmailDto);
    }

    @Operation(summary = "Returns new authorization token")
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    public TokenDto refreshToken(@RequestHeader("Authorization")
                                 @Parameter(description = "Refresh token", required = true) String refreshToken) {
        return userAuthWebService.refreshToken(refreshToken);
    }

    @Operation(summary = "Logs out of the current user, old tokens become invalid")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout() {
        userAuthWebService.logout();
    }

    @Operation(summary = "Deletes current user account. If 2FA is activated, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public AuthResponse<Void> disableUser() {
        return userAuthWebService.disableUser();
    }

    @Operation(summary = "Continues working with confirmation code")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/confirm/{confirmationCode}", method = RequestMethod.GET)
    public void confirm(@PathVariable String confirmationCode) {
        userAuthWebService.confirm(confirmationCode);
    }
}
