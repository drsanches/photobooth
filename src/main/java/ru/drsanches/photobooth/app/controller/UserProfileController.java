package ru.drsanches.photobooth.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.photobooth.app.data.profile.dto.request.ChangeUserProfileDTO;
import ru.drsanches.photobooth.app.data.profile.dto.response.UserInfoDTO;
import ru.drsanches.photobooth.app.service.web.UserProfileWebService;
import ru.drsanches.photobooth.common.swagger.ApiPaginationPage;
import ru.drsanches.photobooth.common.swagger.ApiPaginationSize;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode200;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode400;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode401;
import ru.drsanches.photobooth.common.swagger.ApiResponseCode404;
import ru.drsanches.photobooth.common.swagger.ApiTokenAuthorization;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserProfileController {

    @Autowired
    private UserProfileWebService userProfileWebService;

    @Operation(summary = "Returns current user profile information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.GET)
    public UserInfoDTO getCurrentProfile() {
        return userProfileWebService.getCurrentProfile();
    }

    @Operation(summary = "Sets new profile data for current user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void changeCurrentProfile(@RequestBody ChangeUserProfileDTO changeUserProfileDTO) {
        userProfileWebService.changeCurrentProfile(changeUserProfileDTO);
    }

    @Operation(summary = "[DEPRECATED] Returns a user profile information by username")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @ApiResponseCode404
    @RequestMapping(value = "/search/{username}", method = RequestMethod.GET)
    public UserInfoDTO searchProfile(@PathVariable String username) {
        return userProfileWebService.searchProfile(username);
    }

    @Operation(summary = "Searches a list of user profile information by username")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @ApiResponseCode404
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<UserInfoDTO> searchProfile(
            @Parameter(description = "Username search string") @RequestParam(value = "username") String username,
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size) {
        return userProfileWebService.searchProfile(username, page, size);
    }

    @Operation(summary = "Returns another user profile information by id")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @ApiResponseCode404
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserInfoDTO getProfile(@PathVariable String userId) {
        return userProfileWebService.getProfile(userId);
    }
}
