package com.drsanches.photobooth.app.app.controller;

import com.drsanches.photobooth.app.app.dto.friends.request.RemoveRequestDto;
import com.drsanches.photobooth.app.app.dto.friends.request.SendRequestDto;
import com.drsanches.photobooth.app.app.dto.profile.response.UserInfoDto;
import com.drsanches.photobooth.app.app.service.FriendsWebService;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationPage;
import com.drsanches.photobooth.app.common.swagger.ApiPaginationSize;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode200;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode201;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode400;
import com.drsanches.photobooth.app.common.swagger.ApiResponseCode401;
import com.drsanches.photobooth.app.common.swagger.ApiTokenAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/friends", produces = MediaType.APPLICATION_JSON_VALUE)
public class FriendsController {

    @Autowired
    private FriendsWebService friendsWebService;

    @Operation(summary = "Returns a list of friends information")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<UserInfoDto> getFriends(
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size
    ) {
        return friendsWebService.getFriends(page, size);
    }

    @Operation(summary = "Returns a list of information about users from whom a friend request was received")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public List<UserInfoDto> getIncomingRequests(
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size
    ) {
        return friendsWebService.getIncomingRequests(page, size);
    }

    @Operation(summary = "Returns a list of information about users to whom a friend request was sent")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode401
    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public List<UserInfoDto> getOutgoingRequests(
            @ApiPaginationPage @RequestParam(value = "page", required = false) Integer page,
            @ApiPaginationSize @RequestParam(value = "size", required = false) Integer size
    ) {
        return friendsWebService.getOutgoingRequests(page, size);
    }

    @Operation(summary = "Sends a friend request or confirms of another user's request")
    @ApiTokenAuthorization
    @ApiResponseCode201
    @ApiResponseCode400
    @ApiResponseCode401
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/manage/add", method = RequestMethod.POST)
    public void sendRequest(@RequestBody SendRequestDto sendRequestDto) {
        friendsWebService.sendRequest(sendRequestDto);
    }

    @Operation(summary = "Cancels the friend request from the current or to another user or removes user from friends")
    @ApiTokenAuthorization
    @ApiResponseCode200
    @ApiResponseCode400
    @ApiResponseCode401
    @RequestMapping(path = "/manage/delete", method = RequestMethod.POST)
    public void removeRequest(@RequestBody RemoveRequestDto removeRequestDto) {
        friendsWebService.removeRequest(removeRequestDto);
    }
}
