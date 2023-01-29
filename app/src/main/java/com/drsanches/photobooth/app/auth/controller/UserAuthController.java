package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangeUsernameDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ConfirmationCodeDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.RegistrationDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.UserAuthInfoDTO;
import com.drsanches.photobooth.app.auth.service.web.UserAuthWebService;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangeEmailDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.ChangePasswordDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.request.LoginDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode201;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAuthController {

    @Autowired
    private UserAuthWebService userAuthWebService;

    @Operation(summary = "If 2FA is activated, registers the user, otherwise, sends a confirmation code to the mail")
    @ApiResponseCode200
    @ApiResponseCode400
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public TokenDTO registration(@RequestBody RegistrationDTO registrationDTO) {
        return userAuthWebService.registration(registrationDTO);
    }

    @Operation(summary = "Registers the user by confirmation code")
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registration/confirm", method = RequestMethod.POST)
    public TokenDTO registrationConfirm(@RequestBody ConfirmationCodeDTO confirmationCodeDTO) {
        return userAuthWebService.registrationConfirm(confirmationCodeDTO);
    }

    @Operation(summary = "Returns authorization token")
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        return userAuthWebService.login(loginDTO);
    }

    @Operation(summary = "Returns current user private information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public UserAuthInfoDTO info() {
        return userAuthWebService.info();
    }

    @Operation(summary = "If 2FA is activated, changes username, otherwise, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeUsername", method = RequestMethod.POST)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthWebService.changeUsername(changeUsernameDTO);
    }

    @Operation(summary = "Changes username by confirmation code")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeUsername/confirm", method = RequestMethod.POST)
    public void changeUsernameConfirm(@RequestBody ConfirmationCodeDTO confirmationCodeDTO) {
        userAuthWebService.changeUsernameConfirm(confirmationCodeDTO);
    }

    @Operation(summary = "If 2FA is activated, changes password, otherwise, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthWebService.changePassword(changePasswordDTO);
    }

    @Operation(summary = "Changes username by confirmation code")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changePassword/confirm", method = RequestMethod.POST)
    public void changePasswordConfirm(@RequestBody ConfirmationCodeDTO confirmationCodeDTO) {
        userAuthWebService.changePasswordConfirm(confirmationCodeDTO);
    }

    @Operation(summary = "If 2FA is activated, changes email, otherwise, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
    public void changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthWebService.changeEmail(changeEmailDTO);
    }

    @Operation(summary = "Changes email by confirmation code")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeEmail/confirm", method = RequestMethod.POST)
    public void changeEmailConfirm(@RequestBody ConfirmationCodeDTO confirmationCodeDTO) {
        userAuthWebService.changeEmailConfirm(confirmationCodeDTO);
    }

    @Operation(summary = "Returns new authorization token")
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    public TokenDTO refreshToken(@RequestHeader("Authorization")
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

    @Operation(summary = "If 2FA is activated, deletes current user account, otherwise, sends a confirmation code to the mail")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void disableUser() {
        userAuthWebService.disableUser();
    }

    @Operation(summary = "Deletes current user account")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/deleteUser/confirm", method = RequestMethod.POST)
    public void disableUserConfirm(@RequestBody ConfirmationCodeDTO confirmationCodeDTO) {
        userAuthWebService.disableUserConfirm(confirmationCodeDTO);
    }
}
