package ru.drsanches.photobooth.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.auth.data.dto.ChangeEmailDTO;
import ru.drsanches.photobooth.auth.data.dto.ChangePasswordDTO;
import ru.drsanches.photobooth.auth.data.dto.ChangeUsernameDTO;
import ru.drsanches.photobooth.auth.data.dto.DeleteUserDTO;
import ru.drsanches.photobooth.auth.data.dto.LoginDTO;
import ru.drsanches.photobooth.auth.data.dto.RegistrationDTO;
import ru.drsanches.photobooth.auth.data.dto.TokenDTO;
import ru.drsanches.photobooth.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.photobooth.auth.service.UserAuthWebService;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class UserAuthController {

    @Autowired
    private UserAuthWebService userAuthWebService;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @Operation(summary = "Registers new user account and returns user information")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO registration(@RequestBody RegistrationDTO registrationDTO) {
        return userAuthWebService.registration(registrationDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @Operation(summary = "Returns authorization tokens")
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        return userAuthWebService.login(loginDTO);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @Operation(summary = "Returns current user private information")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public UserAuthInfoDTO info() {
        return userAuthWebService.info();
    }

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    @Operation(summary = "Changes username")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthWebService.changeUsername(changeUsernameDTO);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @Operation(summary = "Changes password")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthWebService.changePassword(changePasswordDTO);
    }

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    @Operation(summary = "Changes email")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthWebService.changeEmail(changeEmailDTO);
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @Operation(summary = "Returns new authorization token")
    public TokenDTO refreshToken(@RequestHeader("Authorization")
                                 @Parameter(description = "Refresh token", required = true) String refreshToken) {
        return userAuthWebService.refreshToken(refreshToken);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @Operation(summary = "Logs out of the current user, old tokens become invalid")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void logout() {
        userAuthWebService.logout();
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @Operation(summary = "Deletes current user account")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void disableUser(@RequestBody DeleteUserDTO deleteUserDTO) {
        userAuthWebService.disableUser(deleteUserDTO);
    }
}
