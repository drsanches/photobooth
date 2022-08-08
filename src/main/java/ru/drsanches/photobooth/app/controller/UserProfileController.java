package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.photobooth.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.photobooth.app.service.web.UserProfileWebService;

@RestController
@RequestMapping(value = "/api/v1/profile")
public class UserProfileController {

    @Autowired
    private UserProfileWebService userProfileWebService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Operation(summary = "Returns current user profile information")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public UserInfoDTO getCurrentProfile() {
        return userProfileWebService.getCurrentProfile();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @Operation(summary = "Sets new profile data for current user")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public void changeCurrentProfile(@RequestBody ChangeUserProfileDTO changeUserProfileDTO) {
        userProfileWebService.changeCurrentProfile(changeUserProfileDTO);
    }

    @RequestMapping(value = "/search/{username}", method = RequestMethod.GET)
    @Operation(summary = "Returns another user profile information by username")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public UserInfoDTO searchProfile(@PathVariable String username) {
        return userProfileWebService.searchProfile(username);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @Operation(summary = "Returns another user profile information by id")
    @Parameter(name = "Authorization", description = "Access token", in = ParameterIn.HEADER, required = true)
    public UserInfoDTO getProfile(@PathVariable String userId) {
        return userProfileWebService.getProfile(userId);
    }
}
