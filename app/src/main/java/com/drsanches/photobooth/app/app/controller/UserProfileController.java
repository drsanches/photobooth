package com.drsanches.photobooth.app.app.controller;

import com.drsanches.photobooth.app.app.dto.profile.request.ChangeUserProfileDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.app.service.UserProfileWebService;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationPage;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationSize;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode404;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/app/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@MonitorTime
public class UserProfileController {

    @Autowired
    private UserProfileWebService userProfileWebService;

    @Operation(summary = "Returns current user profile information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.GET)
    public UserInfoDto getCurrentProfile() {
        return userProfileWebService.getCurrentProfile();
    }

    @Operation(summary = "Sets new profile data for current user")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void changeCurrentProfile(@RequestBody ChangeUserProfileDto changeUserProfileDto) {
        userProfileWebService.changeCurrentProfile(changeUserProfileDto);
    }

    @Operation(summary = "Returns a list of user profile information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @ApiResponseCode404
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<UserInfoDto> searchProfile(
            @Parameter(description = "Username search string") @RequestParam(value = "username") String username,
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size
    ) {
        return userProfileWebService.searchProfile(username, page, size);
    }

    @Operation(summary = "Returns user profile information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @ApiResponseCode404
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserInfoDto getProfile(@PathVariable String userId) {
        return userProfileWebService.getProfile(userId);
    }
}
