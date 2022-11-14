package ru.drsanches.photobooth.auth.controller;

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
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeEmailDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangePasswordDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.ChangeUsernameDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.LoginDTO;
import ru.drsanches.photobooth.auth.data.common.dto.request.RegistrationDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.TokenDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.service.web.UserAuthWebService;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode201;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;
import ru.drsanches.photobooth.common.swagger.ApiTokenAuthorization;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAuthController {

    @Autowired
    private UserAuthWebService userAuthWebService;

    @Operation(summary = "Registers new user account and returns authorization token")
    @ApiResponseCode201
    @ApiResponseCode400
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public TokenDTO registration(@RequestBody RegistrationDTO registrationDTO) {
        return userAuthWebService.registration(registrationDTO);
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

    @Operation(summary = "Changes username")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthWebService.changeUsername(changeUsernameDTO);
    }

    @Operation(summary = "Changes password")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthWebService.changePassword(changePasswordDTO);
    }

    @Operation(summary = "Changes email")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    public void changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthWebService.changeEmail(changeEmailDTO);
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

    @Operation(summary = "Deletes current user account")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void disableUser() {
        userAuthWebService.disableUser();
    }
}
